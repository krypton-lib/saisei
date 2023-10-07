package saisei.io.slice.impl

import naibu.ext.collections.arraycopy
import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toIntRange
import naibu.ext.toLongRange
import naibu.math.toIntSafe
import saisei.io.memory.ShortMemory
import saisei.io.slice.*
import kotlin.js.JsName

/** */
public operator fun ShortArray.get(indices: IntRange): ShortArraySlice = ShortArraySlice(this, indices.toLongRange())

/** */
public operator fun ShortArray.plus(offset: Int): ShortArraySlice = ShortArraySlice(this, offset, size)

/** */
public fun ShortArray.asSlice(offset: Int, length: Int): ShortArraySlice = ShortArraySlice(this, offset, length)

/** */
public fun ShortArray.asSlice(rng: LongRange): ShortArraySlice = ShortArraySlice(this, rng)

/** */
public fun ShortArray.asSlice(rng: IntRange = indices): ShortArraySlice = ShortArraySlice(this, rng.toLongRange())


public class ShortArraySlice(
    @JsName("inner")
    public val array: ShortArray,
    override val range: LongRange,
) : ShortSlice {
    public constructor(array: ShortArray, start: Int = 0, length: Int = array.size) : this(
        array,
        start..<length.toLong()
    )

    init {
        require(range in array.indices) {
            "Specified bounds $range are not within the bounds of the provided array: ${array.indices}"
        }
    }

    override fun get(index: Long): Short = array[boundsCheck(index).toIntSafe()]

    override fun get(index: Long, dst: ShortArray, at: IntRange) {
        boundsCheck(index..<index + at.size)
        arraycopy(array, (range.first + index).toIntSafe(), dst, at.first, at.size)
    }

    override fun get(index: Long, dst: ShortMemory, at: LongRange) {
        boundsCheck(index..<index + at.size)
        dst.store(at.first, array, ((range.first + index)..<at.size).toIntRange())
    }

    override fun get(inner: LongRange): ShortArraySlice = ShortArraySlice(array, calculateInnerRange(inner))

    override fun set(index: Long, value: Short) {
        array[boundsCheck(index).toIntSafe()] = value
    }

    override fun set(index: Long, src: ShortArray, from: IntRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        arraycopy(src, from.first, array, (range.first + index).toIntSafe(), from.size)
    }

    override fun set(index: Long, src: ShortMemory, from: LongRange) {
        require(size - index >= from.size) {
            "Cannot set ${from.size} byte(s) @ $index in this Slice ($indices)"
        }

        src.load(index, array, ((range.first + index)..<from.size).toIntRange())
    }

    override fun contains(value: Short): Boolean = value in array

    override fun resize(range: LongRange): ShortArraySlice {
        if (range !in array.indices) {
            throw IndexOutOfBoundsException("$range is outside of array indices: ${array.indices}")
        }

        return ShortArraySlice(array, range)
    }

    override fun resize(start: Long, end: Long): ShortArraySlice = resize(start..end)

    override fun plus(offset: Long): ShortArraySlice = resize(range.first + offset..range.last + offset)

    override fun array(): ShortArray? = array

    override fun copy(): ShortArraySlice = ShortArraySlice(array, range)

    override fun copyToArray(): ShortArray = array.copyOfRange(range.first.toIntSafe(), (range.last + 1).toIntSafe())

    override fun toString(): String {
        return "ShortArray($array)[$range]"
    }

    override fun contentEquals(other: Slice<Short>): Boolean = when (other) {
        is ShortArraySlice -> other.array.contentEquals(array)
        else -> contentEqualsSlow(other)
    }
}
