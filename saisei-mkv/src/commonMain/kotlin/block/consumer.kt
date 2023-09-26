package saisei.container.mkv.block

import kotlinx.coroutines.flow.map
import naibu.cio.stream.read.SeekableReadStream
import naibu.io.exception.EOFException
import naibu.io.memory.DefaultAllocator
import naibu.io.memory.Memory
import saisei.container.mkv.MatroskaFile
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.matches

fun interface MatroskaBlockConsumer {
    suspend fun consume(stream: SeekableReadStream, block: MatroskaBlock, timecode: Long)
}

suspend fun MatroskaBlock.readFrames(stream: SeekableReadStream, block: suspend (Memory, LongRange) -> Unit) {
    /* start reading every frame from the block */
    val buffer = DefaultAllocator.allocate(frameSizes.max())
    for (i in 0..<frameCount) {
        /* read the frame data into the buffer */
        val range = readFrame(stream, buffer, i)
        block(buffer, range)
    }
}

/**
 *
 */
suspend fun MatroskaFile.readBlocks(stream: SeekableReadStream, consumer: MatroskaBlockConsumer) {
    while (true) try {
        if (stream.position == firstCluster.header.dataPosition) {
            firstCluster.consume { readBlocks(stream, consumer) }
        } else {
            Segment.Cluster.consume(stream) { readBlocks(stream, consumer) }
        }
    } catch (_: EOFException) {
    }
}

/**
 *
 */
@OptIn(ExperimentalSaiseiApi::class)
private suspend fun MasterElementReader.readBlocks(
    stream: SeekableReadStream,
    consumer: MatroskaBlockConsumer,
) {
    var clusterTimestamp = 0L
    readUntilEOF { child ->
        when {
            child matches Segment.Cluster.Timestamp ->
                clusterTimestamp = child.into(Segment.Cluster.Timestamp)!!.read()

            child matches Segment.Cluster.SimpleBlock -> {
                val block = child
                    .into(Segment.Cluster.SimpleBlock)!!
                    .readBlock(stream)

                consumer.consume(stream, block, clusterTimestamp + block.timecode)
            }

            child matches Segment.Cluster.BlockGroup -> child.into(Segment.Cluster.BlockGroup)!!
                .consumeAsFlow()
                .filterIsElement(Segment.Cluster.BlockGroup.Block)
                .map { it.readBlock(stream) }
                .collect { consumer.consume(stream, it, clusterTimestamp) }
        }

        true
    }
}
