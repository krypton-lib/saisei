package saisei.io.slice.impl

import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toLongRange
import saisei.io.memory.ShortMemory
import saisei.io.slice.ShortSlice
import saisei.io.slice.boundsCheck
import saisei.io.slice.calculateInnerRange

/** */
public fun ShortMemory.asSlice(offset: Long, length: Long): ShortMemorySlice = ShortMemorySlice(this, offset, length)

/** */
public fun ShortMemory.asSlice(range: LongRange = indices): ShortMemorySlice = ShortMemorySlice(this, range)

/** */
public operator fun ShortMemory.get(indices: LongRange): ShortMemorySlice = ShortMemorySlice(this, indices)

/** */
public operator fun ShortMemory.get(indices: IntRange): ShortMemorySlice = get(indices.toLongRange())

public class ShortMemorySlice(
    public val memory: ShortMemory,
    override val range: LongRange,
) : ShortSlice {
    public constructor(memory: ShortMemory, offset: Long = 0, limit: Long = memory.size - offset) : this(
        memory,
        offset..<limit
    )

    init {
        require(indices in memory.indices) {
            "Specified bounds $indices are not within the bounds of the provided array: ${memory.indices}"
        }
    }

    override fun get(index: Long): Short = memory.load(boundsCheck(index))

    override fun get(index: Long, dst: ShortArray, at: IntRange) {
        boundsCheck(index..<index + at.size)
        memory.load(range.first + index, dst, at)
    }

    override fun get(index: Long, dst: ShortMemory, at: LongRange) {
        boundsCheck(index..<index + at.size)
        memory.load(range.first + index, dst, at)
    }

    override fun get(inner: LongRange): ShortMemorySlice = ShortMemorySlice(memory, calculateInnerRange(inner))

    override fun set(index: Long, value: Short) {
        memory.store(boundsCheck(index), value)
    }

    override fun set(index: Long, src: ShortArray, from: IntRange) {
        require(size - index >= from.size) {
            "Cannot set ${src.size} byte(s) @ $index in this Slice ($indices)"
        }

        memory.store(range.first + index, src, from)
    }

    override fun set(index: Long, src: ShortMemory, from: LongRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        memory.store(range.first + index, src, from)
    }

    override fun contains(byte: Short): Boolean = any { it == byte }

    override fun get(inner: IntRange): ShortMemorySlice = get(inner.toLongRange())

    override fun resize(range: LongRange): ShortMemorySlice {
        if (range !in memory.indices) {
            throw IndexOutOfBoundsException("Range $range is outside of array indices: ${memory.indices}")
        }

        return ShortMemorySlice(memory, range)
    }

    override fun plus(offset: Long): ShortMemorySlice = resize(range.first + offset..range.last + offset)

    override fun plus(offset: Int): ShortMemorySlice = plus(offset.toLong())

    override fun array(): ShortArray? = null

    override fun copy(): ShortMemorySlice = ShortMemorySlice(memory, indices)

    override fun toString(): String = "$memory[$range]"

}