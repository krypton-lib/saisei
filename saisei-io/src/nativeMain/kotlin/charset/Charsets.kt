package saisei.io.charset

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.plus
import kotlinx.cinterop.readBytes
import naibu.ext.into
import naibu.ext.size
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory
import saisei.io.order.BigEndian
import saisei.io.order.ByteOrder
import saisei.io.order.LittleEndian
import saisei.io.slice.ByteSlice
import saisei.io.slice.cursor.read
import saisei.io.slice.cursor.writer
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.asSlice
import saisei.io.slice.impl.get

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

    public actual val UTF16_LE: Charset get() = Utf16(true)

    public actual val UTF16_BE: Charset get() = Utf16(false)

    internal class Utf16(private val le: Boolean) : Charset {
        private val order: ByteOrder
            get() = if (le) LittleEndian else BigEndian

        override val name: String
            get() = "UTF16_${if (le) "L" else "B"}E"

        override fun decode(src: ByteArray, range: IntRange): String = decode(src[range])

        override fun decode(src: ByteMemory, range: LongRange): String = decode(src[range])

        override fun decode(src: ByteSlice): String = src.read {
            val str = StringBuilder()
            while (!isExhausted) {
                val char = readShort(order).toInt().toChar()
                str.append(char)
            }

            str.toString()
        }

        override fun encode(src: CharSequence, range: IntRange): ByteArray = ByteArray(src.length * 2)
            .asSlice()
            .writer { for (n in 0..<range.size) writeShort(src[n].code.toShort(), order) }
            .into<ByteArraySlice>().array
    }
}
