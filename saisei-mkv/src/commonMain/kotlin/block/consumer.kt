package saisei.container.mkv.block

import kotlinx.coroutines.flow.map
import naibu.cio.stream.read.SeekableReadStream
import naibu.io.exception.EOFException
import saisei.container.mkv.MatroskaSegment
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.intoOrNull
import saisei.io.format.ebml.matches

fun interface MatroskaBlockConsumer {
    suspend fun consume(stream: SeekableReadStream, block: MatroskaBlock, timecode: Long)
}

/**
 *
 */
suspend fun MatroskaSegment.readBlocks(stream: SeekableReadStream, consumer: MatroskaBlockConsumer) {
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
                clusterTimestamp = child.intoOrNull(Segment.Cluster.Timestamp)!!.read()

            child matches Segment.Cluster.SimpleBlock -> {
                val block = child
                    .intoOrNull(Segment.Cluster.SimpleBlock)!!
                    .readBlock(stream)

                consumer.consume(stream, block, clusterTimestamp + block.timecode)
            }

            child matches Segment.Cluster.BlockGroup -> child.intoOrNull(Segment.Cluster.BlockGroup)!!
                .consumeAsFlow()
                .filterIsElement(Segment.Cluster.BlockGroup.Block)
                .map { it.readBlock(stream) }
                .collect { consumer.consume(stream, it, clusterTimestamp) }
        }

        true
    }
}
