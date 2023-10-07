package saisei.io.slice.impl

import kotlinx.cinterop.*
import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toLongRange
import saisei.io.memory.ShortMemory
import saisei.io.slice.ShortSlice
import saisei.io.slice.boundsCheck
import saisei.io.slice.calculateInnerRange

@OptIn(ExperimentalForeignApi::class)
public fun ShortPointerSlice.asShortMemorySlice(): ShortMemorySlice = ShortMemory(pointer, pointerSize)[range]

@OptIn(ExperimentalForeignApi::class)
public class ShortPointerSlice(
    public val pointer: CPointer<ShortVar>,
    public val pointerSize: Long,
    override val range: LongRange,
) : ShortSlice {
    private val offsetPointer get() = pointer.plus(range.first)!!

    override val indices: LongRange get() = 0..<range.size

    override val size: Long get() = indices.size

    override fun get(index: Long): Short = offsetPointer[boundsCheck(index)]

    override fun get(index: Long, dst: ShortArray, at: IntRange) {
        asShortMemorySlice()[index, dst, at]
    }

    override fun get(index: Long, dst: ShortMemory, at: LongRange) {
        asShortMemorySlice()[index, dst, at]
    }

    override fun get(inner: LongRange): ShortPointerSlice =
        ShortPointerSlice(pointer, pointerSize, calculateInnerRange(inner))

    override fun get(inner: IntRange): ShortPointerSlice = get(inner.toLongRange())

    override fun set(index: Long, value: Short) {
        offsetPointer[boundsCheck(index)] = value
    }

    override fun set(index: Long, src: ShortArray, from: IntRange) {
        asShortMemorySlice().set(index, src, from)
    }

    override fun set(index: Long, src: ShortMemory, from: LongRange) {
        asShortMemorySlice().set(index, src, from)
    }

    override fun plus(offset: Long): ShortPointerSlice = resize(range.first + offset..range.last + offset)

    override fun plus(offset: Int): ShortPointerSlice = plus(offset.toLong())

    override fun resize(range: LongRange): ShortPointerSlice {
        val pointerBounds = 0..<pointerSize
        require(range in pointerBounds) {
            "The specified bounds ($range) exceed the bounds of the pointer: $pointerBounds"
        }
        return ShortPointerSlice(pointer, pointerSize, range)
    }

    override fun resize(start: Long, end: Long): ShortPointerSlice = resize(start..end)

    override fun copy(): ShortPointerSlice = ShortPointerSlice(pointer, pointerSize, indices)
}
