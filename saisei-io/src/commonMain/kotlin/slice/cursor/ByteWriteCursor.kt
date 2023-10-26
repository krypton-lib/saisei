package saisei.io.slice.cursor

import kotlinx.atomicfu.locks.synchronized
import naibu.ext.coerceAtMost
import naibu.ext.size
import naibu.ext.toIntRange
import naibu.math.toIntSafe
import saisei.io.order.BigEndian
import saisei.io.order.ByteOrder
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.ByteMemorySlice

public class ByteWriteCursor(slice: ByteSlice) : AbstractByteCursor(slice) {
    public fun write(value: Byte): Unit = synchronized(this) { slice[nextPutIndex()] = value }

    public fun write(src: ByteSlice): Long = synchronized(this) {
        if (isExhausted) return -1

        val range = src.range.coerceAtMost(remaining.toIntSafe())
        when (src) {
            is ByteArraySlice -> slice.set(nextPutIndex(range.size), src.array, range.toIntRange())
            is ByteMemorySlice -> slice.set(nextPutIndex(range.size), src.memory, range)
            else -> for (value in src) write(value)
        }

        range.size
    }

    public fun writeFloat(value: Float, order: ByteOrder = BigEndian): Unit = writer(4) { writeFloat(value, order) }
    public fun writeDouble(value: Double, order: ByteOrder = BigEndian): Unit = writer(8) { writeDouble(value, order) }

    public fun writeShort(value: Short, order: ByteOrder = BigEndian): Unit = writer(2) { writeShort(value, order) }
    public fun writeUShort(value: UShort, order: ByteOrder = BigEndian): Unit = writer(2) { writeUShort(value, order) }

    public fun writeInt(value: Int, order: ByteOrder = BigEndian): Unit = writer(4) { writeInt(value, order) }
    public fun writeUInt(value: UInt, order: ByteOrder = BigEndian): Unit = writer(4) { writeUInt(value, order) }

    public fun writeLong(value: Long, order: ByteOrder = BigEndian): Unit = writer(8) { writeLong(value, order) }
    public fun writeULong(value: ULong, order: ByteOrder = BigEndian): Unit = writer(8) { writeULong(value, order) }

    public fun view(): ByteSlice = slice[0..<position]

    override fun toString(): String = "WriteCursor[$slice@$position]"

    private fun <T> writer(n: Long, block: ByteSlice.() -> T): T = get("write", n, block)
    private fun nextPutIndex(n: Long = 1): Long = nextIndex("write", n)
}
