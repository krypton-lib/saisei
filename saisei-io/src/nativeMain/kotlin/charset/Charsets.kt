package saisei.io.charset

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.plus
import kotlinx.cinterop.readBytes
import naibu.ext.size
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory

public actual object Charsets {
    @OptIn(ExperimentalForeignApi::class)
    public actual val UTF8: Charset = object : Charset {
        override val name: String get() = "utf8"

        override fun decode(src: ByteArray, range: IntRange): String =
            src.decodeToString(range.first, range.last + 1)

        override fun decode(src: ByteMemory, range: LongRange): String =
            decode(src.pointer.plus(range.first)!!.readBytes(range.size.toIntSafe()))

        override fun encode(src: CharSequence, range: IntRange): ByteArray =
            src.substring(range).encodeToByteArray()
    }

    public actual val ASCII: Charset = SingleByteCharset("US_ASCII", CharArray(128) { it.toChar() }.concatToString())
}
