package saisei.io.slice.impl

import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toLongRange
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.boundsCheck
import saisei.io.slice.calculateInnerRange

/** */
public fun ByteMemory.asSlice(offset: Long, length: Long): ByteMemorySlice = ByteMemorySlice(this, offset, length)

/** */
public fun ByteMemory.asSlice(range: LongRange = indices): ByteMemorySlice = ByteMemorySlice(this, range)

/** */
public operator fun ByteMemory.get(indices: LongRange): ByteMemorySlice = ByteMemorySlice(this, indices)

/** */
public operator fun ByteMemory.get(indices: IntRange): ByteMemorySlice = get(indices.toLongRange())

public class ByteMemorySlice(
    public val memory: ByteMemory,
    override val range: LongRange,
) : ByteSlice {
    public constructor(memory: ByteMemory, offset: Long = 0, limit: Long = memory.size - offset) : this(
        memory,
        offset..<limit
    )

    init {
        require(indices in memory.indices) {
            "Specified bounds $indices are not within the bounds of the provided array: ${memory.indices}"
        }
    }

    override fun get(index: Long): Byte = memory.load(boundsCheck(index))

    override fun get(index: Long, dst: ByteArray, at: IntRange) {
        boundsCheck(index..<index + at.size)
        memory.load(range.first + index, dst, at)
    }

    override fun get(index: Long, dst: ByteMemory, at: LongRange) {
        boundsCheck(index..<index + at.size)
        memory.load(range.first + index, dst, at)
    }

    override fun get(inner: LongRange): ByteMemorySlice =
        ByteMemorySlice(memory, calculateInnerRange(inner))

    override fun set(index: Long, value: Byte) {
        memory.store(boundsCheck(index), value)
    }

    override fun set(index: Long, src: ByteArray, from: IntRange) {
        require(size - index >= from.size) {
            "Cannot set ${src.size} byte(s) @ $index in this Slice ($indices)"
        }

        memory.store(range.first + index, src, from)
    }

    override fun set(index: Long, src: ByteMemory, from: LongRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        memory.store(range.first + index, src, from)
    }

    override fun get(inner: IntRange): ByteMemorySlice = get(inner.toLongRange())

    override fun resize(range: LongRange): ByteMemorySlice {
        if (range !in memory.indices) {
            throw IndexOutOfBoundsException("Range $range is outside of array indices: ${memory.indices}")
        }

        return ByteMemorySlice(memory, range)
    }

    override fun plus(offset: Long): ByteMemorySlice = resize(range.first + offset..range.last + offset)

    override fun plus(offset: Int): ByteMemorySlice = plus(offset.toLong())

    override fun copy(): ByteMemorySlice = ByteMemorySlice(memory, indices)

    override fun toString(): String = "$memory[$range]"
}