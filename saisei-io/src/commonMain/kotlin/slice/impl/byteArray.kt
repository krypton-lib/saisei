package saisei.io.slice.impl

import naibu.ext.*
import naibu.ext.collections.arraycopy
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory
import saisei.io.slice.*
import kotlin.js.JsName

/** */
public operator fun ByteArray.get(indices: IntRange): ByteArraySlice = ByteArraySlice(this, indices.toLongRange())

/** */
public operator fun ByteArray.plus(offset: Int): ByteArraySlice = ByteArraySlice(this, offset, size)

/** */
public fun ByteArray.asSlice(offset: Int, length: Int = size - offset): ByteArraySlice =
    ByteArraySlice(this, offset, length)

/** */
public fun ByteArray.asSlice(rng: LongRange): ByteArraySlice = ByteArraySlice(this, rng)

/** */
public fun ByteArray.asSlice(rng: IntRange = indices): ByteArraySlice = ByteArraySlice(this, rng.toLongRange())


public class ByteArraySlice(
    @JsName("inner")
    public val array: ByteArray,
    override val range: LongRange,
) : ByteSlice {
    public constructor(array: ByteArray, start: Int = 0, length: Int = array.size) : this(
        array,
        start..<length.toLong()
    )

    init {
        require(range in array.indices) {
            "Specified bounds $range are not within the bounds of the provided array: ${array.indices}"
        }
    }

    override fun get(index: Long): Byte = array[boundsCheck(index).toIntSafe()]

    override fun get(index: Long, dst: ByteArray, at: IntRange) {
        boundsCheck(index..<index + at.size)
        arraycopy(array, (range.first + index).toIntSafe(), dst, at.first, at.size)
    }

    override fun get(index: Long, dst: ByteMemory, at: LongRange) {
        val lol = boundsCheck(index..<index + at.size)
        dst.store(at.first, array, (lol offset range.first).toIntRange())
    }

    override fun get(inner: LongRange): ByteArraySlice = ByteArraySlice(array, calculateInnerRange(inner))

    override fun set(index: Long, value: Byte) {
        array[boundsCheck(index).toIntSafe()] = value
    }

    override fun set(index: Long, src: ByteArray, from: IntRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        arraycopy(src, from.first, array, (range.first + index).toIntSafe(), from.size)
    }

    override fun set(index: Long, src: ByteMemory, from: LongRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        src.load(index, array, ((range.first + index)..<from.size).toIntRange())
    }

    override fun contains(value: Byte): Boolean = value in array

    override fun resize(range: LongRange): ByteArraySlice {
        if (range !in array.indices) {
            throw IndexOutOfBoundsException("$range is outside of array indices: ${array.indices}")
        }

        return ByteArraySlice(array, range)
    }

    override fun resize(start: Long, end: Long): ByteArraySlice = resize(start..end)

    override fun plus(offset: Long): ByteArraySlice = resize(range.first + offset..range.last + offset)

    override fun array(): ByteArray? = array

    override fun copy(): ByteArraySlice = ByteArraySlice(array, range)

    override fun copyToArray(): ByteArray = array.copyOfRange(range.first.toIntSafe(), (range.last + 1).toIntSafe())

    override fun toString(): String {
        return "ByteArray($array)[$range]"
    }

    override fun contentEquals(other: Slice<Byte>): Boolean = when (other) {
        is ByteArraySlice -> other.array.contentEquals(array)
        else -> contentEqualsSlow(other)
    }
}
