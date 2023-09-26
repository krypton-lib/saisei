package saisei.container.mkv.block

import naibu.cio.stream.read.SeekableReadStream
import naibu.cio.stream.read.readAsInt
import naibu.cio.stream.read.readShort
import naibu.io.memory.Memory
import naibu.math.toIntSafe
import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.element.BinaryElement
import saisei.io.format.ebml.readVariableEBMLInteger

suspend fun BinaryElement.readBlock(stream: SeekableReadStream): MatroskaBlock {
    require(stream.position == header.dataPosition)

    val trackNumber = stream.readVariableEBMLInteger()
    val timecode = stream.readShort().toLong()

    /* start reading the block flags. */
    val flags = stream.readAsInt()

    // whether this is a key frame.
    val isKeyFrame = flags and 0x80 != 0

    // read frame sizes.
    val frameSizes = readFrameSizes(
        stream,
        flags and 0x06 shr 1
    )

    return MatroskaBlock.Immutable(trackNumber, timecode, isKeyFrame, frameSizes.size / 2, frameSizes)
}

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

internal suspend fun BinaryElement.readFrameSizes(stream: SeekableReadStream, laceType: Int): IntArray {
    val frameSizes: IntArray
    if (laceType != 0) {
        frameSizes = IntArray((stream.readAsInt() + 1) * 2)
        when (laceType) {
            1 -> readXiphLaceSizes(stream, frameSizes)
            2 -> readFixedLaceSizes(stream, frameSizes)
            else -> readEbmlLaceSizes(stream, frameSizes)
        }
    } else {
        frameSizes = IntArray(2)
        frameSizes[0] = header.calculateRemaining(stream.position).toIntSafe()
    }

    return frameSizes
}

internal suspend fun BinaryElement.readXiphLaceSizes(stream: SeekableReadStream, frameSizes: IntArray) {
    var sizeTotal = 0
    for (i in 0..<frameSizes.lastIndex) {
        var value = 0
        do {
            value += stream.readAsInt()
        } while (value == 255)

        frameSizes[i] = value
        sizeTotal += value
    }

    val remaining = header.calculateRemaining(stream.position)
    frameSizes[frameSizes.lastIndex] = remaining.toIntSafe() - sizeTotal
}

internal suspend fun BinaryElement.readFixedLaceSizes(stream: SeekableReadStream, frameSizes: IntArray) {
    val size = header
        .calculateRemaining(stream.position)
        .toIntSafe()

    for (i in frameSizes.indices) frameSizes[i] = size
}

internal suspend fun BinaryElement.readEbmlLaceSizes(stream: SeekableReadStream, frameSizes: IntArray) {
    frameSizes[0] = stream.readVariableEBMLInteger().toIntSafe()

    val sizeTotal = frameSizes.first()
    for (i in 1..<frameSizes.lastIndex) {
        val last = frameSizes[i - 1]
        frameSizes[i] = last + stream.readVariableEBMLInteger(EBMLIntegerType.LaceSigned).toIntSafe()
    }

    frameSizes[frameSizes.lastIndex] = header.calculateRemaining(stream.position).toIntSafe() - sizeTotal
}