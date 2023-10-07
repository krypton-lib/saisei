package saisei.io.slice

import naibu.EmptyByteArray
import naibu.ext.toLongRange
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory

/**
 * Slice
 *
 * @constructor Create empty Slice
 */
public interface ByteSlice : Slice<Byte> {
    public companion object;

    /**
     * An empty slice. Features no-op implementations of every method.
     */
    public object Empty : ByteSlice {
        override val indices: LongRange get() = LongRange.EMPTY

        override val range: LongRange get() = LongRange.EMPTY

        override val size: Long get() = 0

        override fun get(inner: LongRange): ByteSlice = Empty

        override fun get(index: Long): Byte = -1
        override fun get(index: Long, dst: ByteArray, at: IntRange): Unit = Unit
        override fun get(index: Long, dst: ByteMemory, at: LongRange): Unit = Unit


        override fun set(index: Long, value: Byte): Unit = Unit
        override fun set(index: Long, src: ByteArray, from: IntRange): Unit = Unit
        override fun set(index: Long, src: ByteMemory, from: LongRange): Unit = Unit

        override fun plus(offset: Long): ByteSlice = Empty
        override fun contains(value: Byte): Boolean = false
        override fun resize(range: LongRange): ByteSlice = Empty
        override fun array(): ByteArray? = null
        override fun copy(): ByteSlice = Empty
        override fun copyToArray(): ByteArray = EmptyByteArray
    }

    public operator fun get(index: Long, dst: ByteArray, at: IntRange = dst.indices)
    public operator fun get(index: Long, dst: ByteMemory, at: LongRange = dst.indices)

    public fun set(index: Long, src: ByteArray, from: IntRange = src.indices)
    public fun set(index: Long, src: ByteMemory, from: LongRange = src.indices)

    public override operator fun plus(offset: Long): ByteSlice

    override operator fun plus(offset: Int): ByteSlice = plus(offset.toLong())

    override operator fun get(inner: LongRange): ByteSlice

    override operator fun get(inner: IntRange): ByteSlice = get(inner.toLongRange())

    override operator fun contains(value: Byte): Boolean = any { it == value }

    override fun resize(range: LongRange): ByteSlice

    override fun resize(start: Long, end: Long): ByteSlice = resize(start..end)

    override fun copy(): ByteSlice

    /**
     * Copies the contents of this slice to a new byte array.
     */
    public fun copyToArray(): ByteArray = ByteArray(size.toIntSafe()).also { get(range.first, it) }

    /**
     * The underlying byte resource as a byte array.
     * Depending on the implementation, this may be a reference to the
     * actual underlying byte resource.
     */
    public fun array(): ByteArray? = null
}
