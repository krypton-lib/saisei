package saisei.io.charset

import naibu.ext.toIntRange
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.ByteMemorySlice

// TODO: charsets should support reading & writing from byte cursors

public interface Charset {
    /**
     * The name of this charset.
     */
    public val name: String

    /**
     * Decodes the given [src] byte array into a [String] using this [Charset].
     *
     * @param src   Bytes to decode
     * @param range The indices of [src] to decode.
     */
    public fun decode(src: ByteArray, range: IntRange = src.indices): String

    /**
     * Decodes the given [Memory] instanceof into a [String] using this [Charset]
     *
     * @param src   Memory instance to decode
     * @param range The indices of [src] to decode.
     */
    public fun decode(src: ByteMemory, range: LongRange = src.indices): String

    /**
     * Decodes the given [Memory] instanceof into a [String] using this [Charset]
     *
     * @param src Slice instance to decode
     */
    public fun decode(src: ByteSlice): String = when (src) {
        is ByteMemorySlice -> decode(src.memory, src.range)
        is ByteArraySlice -> decode(src.array, src.range.toIntRange())
        else -> decode(src.copyToArray())
    }

    /**
     * Encodes the given [src] string into a [ByteArray] using this [Charset].
     *
     * @param src   String to encode
     * @param range The indices of [src] to encode
     */
    public fun encode(src: CharSequence, range: IntRange = src.indices): ByteArray
}