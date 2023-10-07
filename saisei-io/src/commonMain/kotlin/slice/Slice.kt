package saisei.io.slice

import naibu.ext.size
import naibu.ext.toLongRange

public interface Slice<T> : Iterable<T> {
    /** The indices being read from the underlying byte resource. */
    public val range: LongRange

    /** The indices of this slice. */
    public val indices: LongRange get() = 0..<size

    /** The size of this slice in bytes. */
    public val size: Long get() = range.size

    /**
     * @return The newly created slice.
     */
    public operator fun plus(offset: Long): Slice<T>

    /**
     * @return The newly created slice.
     */
    public operator fun plus(offset: Int): Slice<T> = plus(offset.toLong())

    /**
     *
     */
    public operator fun get(inner: LongRange): Slice<T>

    /**
     *
     */
    public operator fun get(inner: IntRange): Slice<T> = get(inner.toLongRange())

    /**
     *
     */
    public operator fun get(index: Long): T

    /**
     *
     */
    public operator fun set(index: Long, value: T)

    /**
     * Check whether this [Slice] contains the specified [value].
     *
     * @param value The value to check for.
     */
    public operator fun contains(value: T): Boolean = any { it == value }

    /**
     * Resizes this slice to the specified bounds.
     *
     * @param range  The new range to use for reading the underlying byte resource.
     *
     * @throws kotlin.IndexOutOfBoundsException If [range] is outside the bounds of the underlying resource.
     * @return The resized slice.
     */
    public fun resize(range: LongRange): Slice<T>

    /**
     * Resizes this slice to the specified [start] & [end] bounds.
     */
    public fun resize(start: Long = range.first, end: Long = range.last): Slice<T> = resize(start..end)

    /**
     *
     */
    public fun contentEquals(other: Slice<T>): Boolean = contentEqualsSlow(other)


    /**
     * Copies this slice.
     */
    public fun copy(): Slice<T>

    /**
     */
    override operator fun iterator(): Iterator<T> = SliceIterator(this)
}