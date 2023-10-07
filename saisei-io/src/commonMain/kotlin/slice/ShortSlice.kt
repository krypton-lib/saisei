package saisei.io.slice

import naibu.ext.toLongRange
import naibu.math.toIntSafe
import saisei.io.memory.ShortMemory

/**
 * Slice
 *
 * @constructor Create empty Slice
 */
public interface ShortSlice : Slice<Short> {
    public companion object;

    /**
     * An empty slice. Features no-op implementations of every method.
     */
    public object Empty : ShortSlice {
        override val indices: LongRange get() = LongRange.EMPTY

        override val range: LongRange get() = LongRange.EMPTY

        override val size: Long get() = 0

        override fun get(inner: LongRange): ShortSlice = Empty

        override fun get(index: Long): Short = -1
        override fun get(index: Long, dst: ShortArray, at: IntRange): Unit = Unit
        override fun get(index: Long, dst: ShortMemory, at: LongRange): Unit = Unit


        override fun set(index: Long, value: Short): Unit = Unit
        override fun set(index: Long, src: ShortArray, from: IntRange): Unit = Unit
        override fun set(index: Long, src: ShortMemory, from: LongRange): Unit = Unit

        override fun plus(offset: Long): ShortSlice = Empty
        override fun contains(value: Short): Boolean = false
        override fun resize(range: LongRange): ShortSlice = Empty
        override fun array(): ShortArray? = null
        override fun copy(): ShortSlice = Empty
        override fun copyToArray(): ShortArray = EmptyShortArray
    }

    public operator fun get(index: Long, dst: ShortArray, at: IntRange = dst.indices)
    public operator fun get(index: Long, dst: ShortMemory, at: LongRange = dst.indices)

    public fun set(index: Long, src: ShortArray, from: IntRange = src.indices)
    public fun set(index: Long, src: ShortMemory, from: LongRange = src.indices)

    override operator fun plus(offset: Long): ShortSlice

    override operator fun plus(offset: Int): ShortSlice = plus(offset.toLong())

    override operator fun get(inner: LongRange): ShortSlice

    override operator fun get(inner: IntRange): ShortSlice = get(inner.toLongRange())

    override fun resize(range: LongRange): ShortSlice

    override fun resize(start: Long, end: Long): ShortSlice = resize(start..end)

    override fun copy(): ShortSlice

    /**
     * The underlying byte resource as a byte array.
     * Depending on the implementation, this may be a reference to the
     * actual underlying byte resource.
     */
    public fun array(): ShortArray? = null

    /**
     * Copies the contents of this slice to a new byte array.
     */
    public fun copyToArray(): ShortArray = ShortArray(size.toIntSafe()).also { get(0, it) }
}
