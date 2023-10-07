package saisei.io.charset

import naibu.ext.size
import naibu.ext.toIntRange
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.ByteMemorySlice
import java.nio.charset.Charset as NioCharset

@JvmInline
public value class CharsetJvm(private val inner: NioCharset) : Charset {
    override val name: String get() = inner.name()

    override fun encode(src: CharSequence, range: IntRange): ByteArray = src.substring(range).toByteArray(inner)

    override fun decode(src: ByteMemory, range: LongRange): String {
        /* duplicate the src buffer, so we can apply the given range. */
        val buf = src.buffer.duplicate()
        buf.position(range.first.toIntSafe())
        buf.limit(range.last.toIntSafe() + 1)

        /* decode the duplicated buffer and then append the result to the output string builder. */
        return inner.decode(buf).toString()
    }

    override fun decode(src: ByteArray, range: IntRange): String =
        String(src, range.first, range.size, inner)

    override fun decode(src: ByteSlice): String = when (src) {
        is ByteMemorySlice -> decode(src.memory, src.range)
        is ByteArraySlice -> decode(src.array, src.range.toIntRange())
        else -> decode(src.copyToArray())
    }
}