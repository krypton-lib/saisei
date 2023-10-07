package saisei.io.slice.impl

import kotlinx.cinterop.*
import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toLongRange
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.boundsCheck
import saisei.io.slice.calculateInnerRange

@OptIn(ExperimentalForeignApi::class)
public fun BytePointerSlice.asByteMemorySlice(): ByteMemorySlice = ByteMemory(pointer, pointerSize)[range]

@OptIn(ExperimentalForeignApi::class)
public class BytePointerSlice(
    public val pointer: CPointer<ByteVar>,
    public val pointerSize: Long,
    override val range: LongRange,
) : ByteSlice {
    private val offsetPointer get() = pointer.plus(range.first)!!

    override val indices: LongRange get() = 0..<range.size

    override val size: Long get() = indices.size

    override fun get(index: Long): Byte = offsetPointer[boundsCheck(index)]

    override fun get(index: Long, dst: ByteArray, at: IntRange) {
        asByteMemorySlice()[index, dst, at]
    }

    override fun get(index: Long, dst: ByteMemory, at: LongRange) {
        asByteMemorySlice()[index, dst, at]
    }

    override fun get(inner: LongRange): BytePointerSlice =
        BytePointerSlice(pointer, pointerSize, calculateInnerRange(inner))

    override fun get(inner: IntRange): BytePointerSlice = get(inner.toLongRange())

    override fun set(index: Long, value: Byte) {
        offsetPointer[boundsCheck(index)] = value
    }

    override fun set(index: Long, src: ByteArray, from: IntRange) {
        asByteMemorySlice().set(index, src, from)
    }

    override fun set(index: Long, src: ByteMemory, from: LongRange) {
        asByteMemorySlice().set(index, src, from)
    }

    override fun plus(offset: Long): BytePointerSlice = resize(range.first + offset..range.last + offset)

    override fun plus(offset: Int): BytePointerSlice = plus(offset.toLong())

    override fun resize(range: LongRange): BytePointerSlice {
        val pointerBounds = 0..<pointerSize
        require(range in pointerBounds) {
            "The specified bounds ($range) exceed the bounds of the pointer: $pointerBounds"
        }
        return BytePointerSlice(pointer, pointerSize, range)
    }

    override fun resize(start: Long, end: Long): BytePointerSlice = resize(start..end)

    override fun copy(): BytePointerSlice = BytePointerSlice(pointer, pointerSize, indices)

    override fun copyToArray(): ByteArray = offsetPointer.readBytes(size.toIntSafe())
}
