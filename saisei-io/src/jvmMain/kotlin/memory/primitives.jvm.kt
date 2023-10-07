package saisei.io.memory

import naibu.ext.size
import naibu.math.toIntSafe

public actual inline fun ByteMemory.loadShort(index: Long): Short {
    return buffer.getShort(index.toIntSafe())
}

public actual inline fun ByteMemory.loadInt(index: Long): Int {
    return buffer.getInt(index.toIntSafe())
}

public actual inline fun ByteMemory.loadLong(index: Long): Long {
    return buffer.getLong(index.toIntSafe())
}

public actual inline fun ByteMemory.storeShort(index: Long, value: Short) {
    buffer.putShort(index.toIntSafe(), value)
}

public actual inline fun ByteMemory.storeInt(index: Long, value: Int) {
    buffer.putInt(index.toIntSafe(), value)
}

public actual inline fun ByteMemory.storeLong(index: Long, value: Long) {
    buffer.putLong(index.toIntSafe(), value)
}

public actual fun ByteMemory.fill(element: Byte, range: LongRange) {
    require(range.size <= size) { "$range is outside the bounds of this Memory ($indices)" }
    for (i in range) store(i, element)
}

public actual fun ShortMemory.fill(element: Short, range: LongRange) {
    require(range.size <= size) { "$range is outside the bounds of this Memory ($indices)" }
    for (i in range) store(i, element)
}
