/**
 * This code is ported from [Lavaplayer](https://github.com/sedmelluq/lavaplayer).
 */
package saisei.io.format.ebml

import naibu.ext.asInt
import naibu.io.memory.Memory
import naibu.io.slice.Slice
import naibu.io.slice.asSlice
import naibu.math.toIntSafe

fun Memory.readEBMLInteger_(type: EBMLIntegerType? = null): Long =
    asSlice().readEBMLInteger_(type)

fun Slice.readEBMLInteger_(type: EBMLIntegerType? = null): Long {
    val iter = iterator()
    val codeLen = size.toIntSafe()

    var code: Long = 0
    for (i in 1..codeLen) {
        val nextByte = applyNextByte(iter.next(), codeLen, i)
        code = code or nextByte.toLong()
    }

    return applyType(code, codeLen, type)
}

fun Memory.readEBMLInteger(type: EBMLIntegerType? = null): Long =
    asSlice().readEBMLInteger(type)

fun ByteArray.readEBMLInteger(type: EBMLIntegerType? = null, offset: Int = 0): Long =
    Slice(this, offset).readEBMLInteger(type)

fun Slice.readEBMLInteger(type: EBMLIntegerType? = null): Long {
    val iter = iterator()
    return readEbmlInteger(type) { iter.next() }
}

/**
 * Read an EBML code using [read].
 *
 * @param type  Method of sign handling (null is unsigned)
 * @parma read  Used to read the next byte in line.
 * @return Read EBML code
 */
internal inline fun readEbmlInteger(type: EBMLIntegerType? = null, read: () -> Byte): Long {
    val firstByte = read()
    val codeLength = getCodeLength(firstByte)

    /* put together the integer */
    var code = applyFirstByte(firstByte, codeLength).toLong()
    for (idx in 2..codeLength) {
        val nextByte = applyNextByte(read(), codeLength, idx)
        code = code or nextByte.toLong()
    }

    return applyType(code, codeLength, type)
}

private fun getCodeLength(firstByte: Byte): Int {
    val codeLength = firstByte.countLeadingZeroBits() + 1
    require(codeLength <= 8) {
        "More than 8 bytes for length, probably invalid data"
    }

    return codeLength
}

private fun applyFirstByte(firstByte: Byte, codeLength: Int): Int =
    (firstByte.toInt() and (0xFFL shr codeLength).toInt() shl (codeLength - 1 shl 3))

internal fun applyNextByte(value: Byte, codeLength: Int, index: Int): Int =
    value.asInt() shl ((codeLength - index) shl 3)

internal fun applyType(code: Long, codeLength: Int, type: EBMLIntegerType?): Long = if (type == null) {
    code
} else when (type) {
    EBMLIntegerType.Signed -> signEbmlInteger(code, codeLength)
    EBMLIntegerType.LaceSigned -> laceSignEbmlInteger(code, codeLength)
    else -> code
}

private fun laceSignEbmlInteger(code: Long, codeLength: Int): Long = when (codeLength) {
    1 -> code - 63
    2 -> code - 8191
    3 -> code - 1048575
    4 -> code - 134217727
    else -> throw IllegalStateException("Code length out of bounds.")
}

private fun signEbmlInteger(code: Long, codeLength: Int): Long {
    val mask = getSignMask(codeLength)
    return if (code and mask != 0L) code or mask else code
}

private fun getSignMask(codeLength: Int): Long = when (codeLength) {
    1 -> 0x000000000000003FL.inv()
    2 -> 0x0000000000001FFFL.inv()
    3 -> 0x00000000000FFFFFL.inv()
    4 -> 0x0000000007FFFFFFL.inv()
    5 -> 0x00000003FFFFFFFFL.inv()
    6 -> 0x000001FFFFFFFFFFL.inv()
    7 -> 0x0000FFFFFFFFFFFFL.inv()
    8 -> 0x007FFFFFFFFFFFFFL.inv()
    else -> error("Code length out of bounds.")
}

/**
 * EBML code type (sign handling method).
 */
enum class EBMLIntegerType {
    /**
     * Signed value with first bit marking the sign.
     */
    Signed,

    /**
     * Signed value where sign is applied via subtraction.
     */
    LaceSigned,

    /**
     * Unsigned value.
     */
    Unsigned
}
