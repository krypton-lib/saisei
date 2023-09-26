package saisei.io.format.ebml

import naibu.cio.stream.read.ReadStream

/**
 * Read an EBML code from data input with fixed size - no size encoded in the data.
 *
 * @param codeLength Length of the code in bytes
 * @param type       Method of sign handling (null is unsigned)
 * @return Read EBML code
 */
suspend fun ReadStream.readFixedSizeEBMLInteger(codeLength: Int, type: EBMLIntegerType? = null): Long {
    var code: Long = 0
    for (i in 1..codeLength) {
        val nextByte = applyNextByte(read(), codeLength, i)
        code = code or nextByte.toLong()
    }

    return applyType(code, codeLength, type)
}

/**
 * Read an EBML code from data input.
 *
 * @param type Method of sign handling (null is unsigned)
 * @return Read EBML code
 */
suspend fun ReadStream.readVariableEBMLInteger(type: EBMLIntegerType? = null): Long =
    readEbmlInteger(type) { read() }
