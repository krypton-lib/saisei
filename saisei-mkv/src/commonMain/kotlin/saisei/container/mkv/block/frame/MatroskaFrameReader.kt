package saisei.container.mkv.block.frame

import naibu.common.Closeable
import naibu.common.Resetable
import saisei.container.mkv.MatroskaSegment
import saisei.container.mkv.block.MatroskaBlock
import saisei.container.mkv.block.readBlock
import saisei.container.mkv.element.Segment
import saisei.io.exception.EOFException
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.matches
import saisei.io.memory.ByteMemory
import saisei.io.stream.SeekableReadStream

/**
 *
 */
@OptIn(ExperimentalSaiseiApi::class)
public data class MatroskaFrameReader(
    val segment: MatroskaSegment,
    val stream: SeekableReadStream,
) : Closeable, Resetable {
    private var state: State = State.Idle

    private var cluster: MasterElementReader? = null
    private val buffer = ByteMemory.Allocator.allocate(8192)
    private var clusterTimecode: Long = 0

    /**
     * Whether this reader has been exhausted.
     */
    val isExhausted: Boolean
        get() = state == State.Exhausted

    override fun reset() {
        state = State.Idle
        cluster = null
        clusterTimecode = 0
    }

    override fun close() {
        buffer.close()
        stream.close()
    }

    /**
     * Reads the next frame in this Matroska file, or `null` if there are no more to read.
     *
     * There is no guarantee that any frame read beforehand will still contain its data as a buffer is shared
     * across reads.
     */
    public suspend fun readFrame(): MatroskaFrame? {
        if (isExhausted) {
            return null
        }

        return try {
            if (cluster == null) {
                cluster = segment.firstCluster.reader(stream)
            }

            /* attempt to read a block. */
            if (!state.hasRemaining) while (true) {
                if (cluster?.hasRemaining != true) readCluster()

                val el = cluster?.readNextChild()
                    ?: continue

                when {
                    el matches Segment.Cluster.Timestamp ->
                        clusterTimecode = el.into(Segment.Cluster.Timestamp).read()

                    el matches Segment.Cluster.SimpleBlock -> {
                        val block = el
                            .into(Segment.Cluster.SimpleBlock)
                            .readBlock(stream)

                        state = State.Block(block, stream)
                        break
                    }

                    el matches Segment.Cluster.BlockGroup -> {
                        state = State.BlockGroup(el.into(Segment.Cluster.BlockGroup), stream)
                        break
                    }
                }
            }

            val frame = state.readFrame(buffer)
            frame.copy(timecode = frame.timecode + clusterTimecode)
        } catch (ex: EOFException) {
            state = State.Exhausted
            null
        }
    }

    @OptIn(ExperimentalSaiseiApi::class)
    public suspend fun readCluster() {
        /* if we've exhausted the current cluster then read the next one. */
        while (true) {
            val header = ElementHeader.read(stream)
            if (header.id notMatches Segment.Cluster.id) {
                // TODO: EOF
                header.skip(stream)
                continue
            }

            cluster = Segment.Cluster.reader(stream, header)
            break
        }
    }

    override fun toString(): String {
        return "MatroskaFrameReader(state=$state, cluster=$cluster, stream=$stream)"
    }

    private sealed interface State {
        val hasRemaining: Boolean

        suspend fun readFrame(into: ByteMemory): MatroskaFrame

        data object Idle : State {
            override val hasRemaining: Boolean get() = false
            override suspend fun readFrame(into: ByteMemory): MatroskaFrame = TODO()
        }

        data object Exhausted : State {
            override val hasRemaining: Boolean get() = false
            override suspend fun readFrame(into: ByteMemory): MatroskaFrame = TODO()
        }

        @OptIn(ExperimentalSaiseiApi::class)
        data class BlockGroup(val element: MasterElement, val stream: SeekableReadStream) : State {
            private val reader = element.reader()
            private var block: Block? = null

            override val hasRemaining: Boolean
                get() = reader.remaining > 0

            override suspend fun readFrame(into: ByteMemory): MatroskaFrame {
                if (block?.hasRemaining != true) while (hasRemaining) {
                    val el = reader.readNextChild()
                        ?: continue

                    if (el matches Segment.Cluster.BlockGroup.Block) {
                        block = Block(
                            block = el.into(Segment.Cluster.BlockGroup.Block).readBlock(stream),
                            stream = stream
                        )

                        break
                    }
                }

                return block?.readFrame(into) ?: throw EOFException()
            }
        }

        data class Block(val block: MatroskaBlock, val stream: SeekableReadStream) : State {
            private var index = 0

            override val hasRemaining: Boolean
                get() = index < block.frameCount

            override suspend fun readFrame(into: ByteMemory): MatroskaFrame {
                val range = block.readFrame(stream, into, index++)
                return MatroskaFrame(into, range, block.trackNumber, block.timecode)
            }
        }
    }
}