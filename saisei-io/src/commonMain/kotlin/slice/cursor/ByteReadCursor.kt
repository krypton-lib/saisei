package saisei.io.slice.cursor

import kotlinx.atomicfu.locks.synchronized
import naibu.ext.coerceAtMost
import naibu.ext.size
import naibu.ext.toIntRange
import saisei.io.order.BigEndian
import saisei.io.order.ByteOrder
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.ByteMemorySlice

public class ByteReadCursor(slice: ByteSlice) : AbstractByteCursor(slice) {
    public fun read(): Byte = synchronized(this) {
        slice[nextGetIndex()]
    }

    public fun read(n: Long): ByteSlice = synchronized(this) {
        get("read", n)
    }

    public fun read(dst: ByteSlice): Long = synchronized(this) {
        if (isExhausted) return -1

        val range = dst.range.coerceAtMost(remaining)
        when (dst) {
            is ByteArraySlice -> slice[nextGetIndex(range.size), dst.array, range.toIntRange()]
            is ByteMemorySlice -> slice[nextGetIndex(range.size), dst.memory, range]
            else -> for (i in dst.indices) dst[i] = read()
        }

        range.size
    }

    public fun readFloat(order: ByteOrder = BigEndian): Float = reader(4) { readFloat(order) }
    public fun readDouble(order: ByteOrder = BigEndian): Double = reader(8) { readDouble(order) }

    public fun readShort(order: ByteOrder = BigEndian): Short = reader(2) { readShort(order) }
    public fun readUShort(order: ByteOrder = BigEndian): UShort = reader(2) { readUShort(order) }

    public fun readInt(order: ByteOrder = BigEndian): Int = reader(4) { readInt(order) }
    public fun readUInt(order: ByteOrder = BigEndian): UInt = reader(4) { readUInt(order) }

    public fun readLong(order: ByteOrder = BigEndian): Long = reader(8) { readLong(order) }
    public fun readULong(order: ByteOrder = BigEndian): ULong = reader(8) { readULong(order) }

    override fun toString(): String = "ReadCursor(slice=$slice, position=$position)"

    private fun <T> reader(n: Long, block: ByteSlice.() -> T): T = get("read", n, block)
    private fun nextGetIndex(n: Long = 1): Long = nextIndex("read", n)
}
