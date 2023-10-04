package saisei.container.mkv.block.frame

import naibu.cio.stream.read.SeekableReadStream
import naibu.io.exception.EOFException
import naibu.io.memory.DefaultAllocator
import naibu.io.memory.Memory
import saisei.container.mkv.MatroskaSegment
import saisei.container.mkv.block.MatroskaBlock
import saisei.container.mkv.block.readBlock
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.matches

// TODO: Discard Padding

/**
 *
 */
@OptIn(ExperimentalSaiseiApi::class)
data class MatroskaFrameReader(
    val segment: MatroskaSegment,
    val stream: SeekableReadStream,
) {
    private var state: State = State.Idle

    private val buffer = DefaultAllocator.allocate(8192)
    private var cluster: MasterElementReader = segment.firstCluster.reader()
    private var clusterTimecode: Long = 0

    /**
     * Whether this reader has been exhausted.
     */
    val isExhausted: Boolean
        get() = state == State.Exhausted

    fun reset() {
        state = State.Idle
        cluster = segment.firstCluster.reader()
        clusterTimecode = 0
    }

    /**
     * Reads the next frame in this Matroska file, or `null` if there are no more to read.
     *
     * There is no guarantee that any frame read beforehand will still contain its data as a buffer is shared
     * across reads.
     */
    suspend fun readFrame(): MatroskaFrame? {
        if (isExhausted) {
            return null
        }

        return try {
            /* attempt to read a block. */
            if (!state.hasRemaining) while (true) {
                if (!cluster.hasRemaining) readCluster()

                val el = cluster.readNextChild()
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

                    else -> cluster.skipLastChild()
                }
            }

            val frame = state.readFrame(buffer)
//            frame.copy(timecode = file.convertTimecodeToDuration(frame.timecode + clusterTimecode).inWholeMilliseconds)
            frame.copy(timecode = frame.timecode + clusterTimecode)
        } catch (ex: EOFException) {
            state = State.Exhausted
            null
        }
    }

    suspend fun readCluster() {
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

        suspend fun readFrame(into: Memory): MatroskaFrame

        data object Idle : State {
            override val hasRemaining: Boolean get() = false
            override suspend fun readFrame(into: Memory): MatroskaFrame = TODO()
        }

        data object Exhausted : State {
            override val hasRemaining: Boolean get() = false
            override suspend fun readFrame(into: Memory): MatroskaFrame = TODO()
        }

        data class BlockGroup(val element: MasterElement, val stream: SeekableReadStream) : State {
            private val reader = element.reader()
            private var block: Block? = null

            override val hasRemaining: Boolean
                get() = reader.remaining > 0

            override suspend fun readFrame(into: Memory): MatroskaFrame {
                if (block?.hasRemaining != false) while (hasRemaining) {
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

            override suspend fun readFrame(into: Memory): MatroskaFrame {
                val range = block.readFrame(stream, into, index++)
                return MatroskaFrame(into, range, block.trackNumber, block.timecode)
            }
        }
    }
}
