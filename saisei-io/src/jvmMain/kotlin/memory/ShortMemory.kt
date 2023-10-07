package saisei.io.memory

import naibu.ext.size
import naibu.ext.toIntRange
import naibu.math.toIntSafe
import saisei.io.order.ByteOrder
import saisei.io.order.LittleEndian
import saisei.io.order.nio
import saisei.io.order.saisei
import java.nio.ByteBuffer
import java.nio.ShortBuffer

public actual class ShortMemory internal constructor(
    private val byteBuffer: ByteBuffer,
) : Memory<Short> {
    public val buffer: ShortBuffer = byteBuffer.asShortBuffer()

    override val size: Long get() = buffer.capacity().toLong()
    override val order: ByteOrder get() = buffer.order().saisei

    override fun load(index: Long): Short = buffer.get(index.toIntSafe())

    override fun store(index: Long, value: Short) {
        buffer.put(index.toIntSafe(), value)
    }

    public actual fun load(index: Long, dst: ShortArray, at: IntRange) {
        buffer.get(index.toIntSafe(), dst, at.first, at.size)
    }

    public actual fun store(index: Long, src: ShortArray, from: IntRange) {
        buffer.put(index.toIntSafe(), src, from.first, from.size)
    }

    public actual fun load(index: Long, dst: ShortMemory, at: LongRange) {
        val at32 = at.toIntRange()
        dst.buffer.put(at32.first, buffer, index.toIntSafe(), at32.size)
    }

    public actual fun store(index: Long, src: ShortMemory, from: LongRange) {
        val from32 = from.toIntRange()
        buffer.put(index.toIntSafe(), src.buffer, from32.first, from32.size)
    }

    override fun close(): Unit = Allocator.release(this)

    /**
     * View this [ShortMemory] as a [ByteMemory].
     * **Beware!** Any modifications done to the returned memory instance will be reflected in this short memory instance.
     */
    public actual fun bytes(): ByteMemory = ByteMemory(byteBuffer)

    public actual data object Allocator {
        public actual fun allocate(size: Int): ShortMemory = ByteMemory.Allocator
            .allocate(size * 2)
            .shorts()

        public actual fun allocate(size: Long): ShortMemory = allocate(size.toIntSafe())

        public actual fun release(chunk: ShortMemory): Unit = Unit
    }
}