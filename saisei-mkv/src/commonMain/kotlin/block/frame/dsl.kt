package saisei.container.mkv.block.frame

import saisei.container.mkv.block.MatroskaBlock
import saisei.io.memory.ByteMemory
import saisei.io.stream.SeekableReadStream

public suspend fun MatroskaBlock.readFrame(stream: SeekableReadStream, buffer: ByteMemory, index: Int): LongRange {
    require(index < frameCount) {
        "Frame Index is out of Bounds."
    }

    val frameSize = frameSizes[index]
    require(frameSize <= buffer.size) {
        "Provided buffer is not large enough for Frame #$index ($frameSize > ${buffer.size})"
    }

    val range = 0L..<frameSize
    stream.readFully(buffer, range)

    return range
}

public suspend fun MatroskaBlock.readFrames(
    stream: SeekableReadStream,
    buffer: ByteMemory = ByteMemory.Allocator.allocate(frameSizes.max()),
    block: suspend (ByteMemory, LongRange) -> Unit,
) {
    /* start reading every frame from the block */
    for (i in 0..<frameCount) {
        /* read the frame data into the buffer */
        val range = readFrame(stream, buffer, i)
        block(buffer, range)
    }
}
