package saisei.io.slice

import naibu.ext.contains
import naibu.ext.size
import naibu.ext.toIntRange
import naibu.math.toIntSafe
import saisei.io.memory.ByteMemory
import saisei.io.memory.ShortMemory
import saisei.io.slice.impl.asSlice

public val EmptyShortArray: ShortArray = ShortArray(0)

public inline val Slice<*>.size32: Int get() = size.toIntSafe()
public inline val Slice<*>.range32: IntRange get() = range.toIntRange()

public operator fun <T> Slice<T>.get(idx: Int): T = get(idx.toLong())

public operator fun <T> Slice<T>.set(idx: Int, value: T): Unit = set(idx.toLong(), value)

public operator fun ShortSlice.set(idx: Int, value: ShortArray): Unit = set(idx.toLong(), value)

public operator fun ShortSlice.set(idx: Long, value: ShortArray): Unit = set(idx, value)

public operator fun ByteSlice.set(idx: Int, value: ByteArray): Unit = set(idx.toLong(), value)

public operator fun ByteSlice.set(idx: Long, value: ByteArray): Unit = set(idx, value)

/** */
public fun Slice<*>.contentToString(): String = joinToString(", ", prefix = "[", postfix = "]")

/** */
public fun ShortSlice.contentEquals(other: ShortArray): Boolean = contentEquals(other.asSlice())

/** */
public fun ShortSlice.contentEquals(other: ShortMemory): Boolean = contentEquals(other.asSlice())

/** */
public fun ByteSlice.contentEquals(other: ByteArray): Boolean = contentEquals(other.asSlice())

/** */
public fun ByteSlice.contentEquals(other: ByteMemory): Boolean = contentEquals(other.asSlice())

internal fun Slice<*>.calculateInnerRange(inner: LongRange): LongRange {
    if (inner == range || inner.size == size || inner.first == range.first) {
        return inner
    }

    boundsCheck(inner)
    val first = range.first + inner.first
    val last = (first + (range.size - inner.size) + 1)
//    println("$first $last, ${range.size} ${range.first}, ${inner.size} ${inner.first}")

    return (first..last)
}

internal fun <T> Slice<T>.contentEqualsSlow(b: Slice<T>): Boolean {
    if (size != b.size) return false
    return withIndex().all { (i, e) -> b[i] == e }
}


internal fun Slice<*>.boundsCheck(range: LongRange): LongRange {
    require(range in indices) { "Range $range is outside the bounds of this Slice ($indices)" }
    return range
}

internal fun Slice<*>.boundsCheck(idx: Long): Long {
    if (idx in indices) return range.first + idx
    throw IndexOutOfBoundsException("Index $idx is outside the bounds of this Slice ($indices)")
}
