package saisei.io.charset

import naibu.ext.asInt
import naibu.ext.size
import naibu.ext.toIntRange
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.get
import saisei.io.slice.impl.asSlice

public class SingleByteCharset(override val name: String, private val conv: String) : Charset {
    private val codes = conv
        .withIndex()
        .associate { (i, c) -> c.code to i }

    override fun decode(src: ByteArray, range: IntRange): String = decode(src.asSlice(range))

    override fun decode(src: ByteMemory, range: LongRange): String = decode(src.asSlice(range))

    override fun decode(src: ByteSlice): String {
        val out = StringBuilder()
        for (i in src.indices.toIntRange()) out.append(conv[src[i].asInt()])
        return out.toString()
    }

    override fun encode(src: CharSequence, range: IntRange): ByteArray {
        val out = ByteArray(range.size)
        for ((i, n) in src.indices.withIndex()) {
            val c = src[n].code
            out[i] = if (c in codes) (codes[c] ?: 0).toByte() else '?'.code.toByte()
        }

        return out
    }
}
