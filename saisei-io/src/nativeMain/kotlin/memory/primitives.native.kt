@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package saisei.io.memory

import kotlinx.cinterop.*
import naibu.ext.size
import platform.posix.memset
import saisei.io.slice.impl.BytePointerSlice
import kotlin.experimental.ExperimentalNativeApi

@PublishedApi
internal fun ByteMemory.slice(i: Long, n: Int): BytePointerSlice =
    BytePointerSlice(pointer, size, checkBoundsL(i, n)..<n)

public actual inline fun ByteMemory.loadShort(index: Long): Short {
    checkBoundsL(index, 2)
    return if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<ShortVar>().pointed.value
    } else {
        order.getShort(slice(index, 2))
    }
}

public actual inline fun ByteMemory.loadInt(index: Long): Int {
    checkBoundsL(index, 4)
    return if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<IntVar>().pointed.value
    } else {
        order.getInt(slice(index, 4))
    }
}

public actual inline fun ByteMemory.loadLong(index: Long): Long {
    checkBoundsL(index, 8)
    return if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<LongVar>().pointed.value
    } else {
        order.getLong(slice(index, 8))
    }
}

public actual inline fun ByteMemory.storeShort(index: Long, value: Short) {
    checkBoundsL(index, 2)
    if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<ShortVar>().pointed.value = value
    } else {
        order.putShort(value, slice(index, 2))
    }
}

public actual inline fun ByteMemory.storeInt(index: Long, value: Int) {
    checkBoundsL(index, 4)
    if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<IntVar>().pointed.value = value
    } else {
        order.putInt(value, slice(index, 4))
    }
}

public actual inline fun ByteMemory.storeLong(index: Long, value: Long) {
    checkBoundsL(index, 8)
    if (Platform.canAccessUnaligned) {
        pointer.plus(index)!!.reinterpret<LongVar>().pointed.value = value
    } else {
        order.putLong(value, slice(index, 8))
    }
}

public actual fun ByteMemory.fill(element: Byte, range: LongRange) {
    require(range.size <= size) { "$range is outside the bounds of this Memory ($indices)" }
    memset(pointer + range.first, 0, range.size.toULong())
}

public actual fun ShortMemory.fill(element: Short, range: LongRange) {
    require(range.size <= size) { "$range is outside the bounds of this Memory ($indices)" }
    memset(pointer + range.first, 0, range.size.toULong())
}
