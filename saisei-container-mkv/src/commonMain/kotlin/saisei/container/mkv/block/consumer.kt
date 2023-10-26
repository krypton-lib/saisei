package saisei.container.mkv.block

import kotlinx.coroutines.flow.map
import saisei.container.mkv.MatroskaSegment
import saisei.container.mkv.element.Segment
import saisei.io.exception.EOFException
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.matches
import saisei.io.stream.SeekableReadStream

public fun interface MatroskaBlockConsumer {
    public suspend fun consume(stream: SeekableReadStream, block: MatroskaBlock, timecode: Long)
}

/**
 *
 */
public suspend fun MatroskaSegment.readBlocks(stream: SeekableReadStream, consumer: MatroskaBlockConsumer) {
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
                clusterTimestamp = child
                    .into(Segment.Cluster.Timestamp)
                    .read()

            child matches Segment.Cluster.SimpleBlock -> {
                val block = child
                    .into(Segment.Cluster.SimpleBlock)
                    .readBlock(stream)

                consumer.consume(stream, block, clusterTimestamp + block.timecode)
            }

            child matches Segment.Cluster.BlockGroup -> child.into(Segment.Cluster.BlockGroup)
                .consumeAsFlow()
                .filterIsElement(Segment.Cluster.BlockGroup.Block)
                .map { it.readBlock(stream) }
                .collect { consumer.consume(stream, it, clusterTimestamp) }
        }

        true
    }
}
