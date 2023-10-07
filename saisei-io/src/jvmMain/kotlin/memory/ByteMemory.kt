package saisei.io.memory

import naibu.ext.size
import naibu.ext.toIntRange
import naibu.math.toIntSafe
import saisei.io.order.ByteOrder
import saisei.io.order.nio
import saisei.io.order.saisei
import java.nio.ByteBuffer

public actual class ByteMemory internal constructor(public val buffer: ByteBuffer) : Memory<Byte> {
    override val size: Long get() = buffer.capacity().toLong()
    override val order: ByteOrder get() = buffer.order().saisei

    override fun load(index: Long): Byte =
        buffer.get(index.toIntSafe())

    public actual fun load(index: Long, dst: ByteArray, at: IntRange) {
        buffer.get(index.toIntSafe(), dst, at.first, at.size)
    }

    public actual fun load(index: Long, dst: ByteMemory, at: LongRange) {
        val at32 = at.toIntRange()
        dst.buffer.put(at32.first, buffer, index.toIntSafe(), at32.size)
    }

    override fun store(index: Long, value: Byte) {
        buffer.put(index.toIntSafe(), value)
    }

    public actual fun store(index: Long, src: ByteArray, from: IntRange) {
        buffer.put(index.toIntSafe(), src, from.first, from.size)
    }

    public actual fun store(index: Long, src: ByteMemory, from: LongRange) {
        val from32 = from.toIntRange()
        buffer.put(index.toIntSafe(), src.buffer, from32.first, from32.size)
    }

    override fun close() {
        Allocator.release(this)
    }

    public actual fun shorts(): ShortMemory = ShortMemory(buffer)

    override fun toString(): String = "ByteMemory(buffer=$buffer)"

    public actual data object Allocator {
        public actual fun allocate(size: Int): ByteMemory {
            val buffer = ByteBuffer
                .allocateDirect(size)
                .order(ByteOrder.Native.nio)
            return ByteMemory(buffer)
        }

        public actual fun allocate(size: Long): ByteMemory = allocate(size.toIntSafe())

        public actual fun release(chunk: ByteMemory) {
        }

        public fun from(value: ByteBuffer): ByteMemory {
            require(value.isDirect)
            return ByteMemory(value.duplicate())
        }
    }
}