package saisei.container.mkv.block.frame

import naibu.cio.stream.read.SeekableReadStream
import naibu.io.memory.DefaultAllocator
import naibu.io.memory.Memory
import saisei.container.mkv.block.MatroskaBlock

suspend fun MatroskaBlock.readFrame(stream: SeekableReadStream, buffer: Memory, index: Int): LongRange {
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

suspend fun MatroskaBlock.readFrames(stream: SeekableReadStream, block: suspend (Memory, LongRange) -> Unit) {
    /* start reading every frame from the block */
    val buffer = DefaultAllocator.allocate(frameSizes.max())
    for (i in 0..<frameCount) {
        /* read the frame data into the buffer */
        val range = readFrame(stream, buffer, i)
        block(buffer, range)
    }
}
