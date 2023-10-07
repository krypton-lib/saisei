package saisei.io.memory

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.*
import naibu.ext.size
import platform.posix.memcpy
import saisei.io.order.ByteOrder

@OptIn(ExperimentalForeignApi::class)
public actual class ShortMemory internal constructor(
    public val pointer: CPointer<ShortVar>,
    override val size: Long,
) : Memory<Short>, SynchronizedObject() {
    override val order: ByteOrder get() = ByteOrder.Native

    override fun load(index: Long): Short = synchronized(this) {
        pointer[index]
    }

    override fun store(index: Long, value: Short): Unit = synchronized(this) {
        pointer[index] = value
    }

    public actual fun load(index: Long, dst: ShortArray, at: IntRange): Unit = synchronized(this) {
        dst.usePinned {
            val self = pointer + checkBoundsL(index, at.size)
            memcpy(it.addressOf(at.first), self, at.size.convert())
        }
    }

    public actual fun load(index: Long, dst: ShortMemory, at: LongRange): Unit = synchronized(this) {
        memcpy(
            dst.pointer + dst.checkBoundsI(at.first, at.size),
            pointer + checkBoundsI(index, at.size),
            at.size.convert()
        )
    }

    public actual fun store(index: Long, src: ShortArray, from: IntRange): Unit = synchronized(this) {
        src.usePinned {
            val self = pointer + checkBoundsI(index, from.size.toLong())
            memcpy(self, it.addressOf(from.first), from.size.toULong())
        }
    }

    public actual fun store(index: Long, src: ShortMemory, from: LongRange): Unit = synchronized(this) {
        src.load(from.first, this, checkBoundsI(index, from.size)..<from.size)
    }

    override fun close() {
        Allocator.release(this)
    }

    /**
     * View this [ShortMemory] as a [ByteMemory].
     *
     * **Beware!** Any modifications done to the returned memory instance will be reflected in this short memory
     * instance.
     */
    public actual fun bytes(): ByteMemory = ByteMemory(pointer.reinterpret(), size / 2)

    private inline fun checkBoundsL(idx: Long, n: Int = 1): Long {
        if (size - n < idx) throw IndexOutOfBoundsException("Index $idx is outside of ... ")
        return idx
    }

    private inline fun checkBoundsI(idx: Long, n: Long = 1): Long {
        if (size - n < idx) throw IndexOutOfBoundsException("Index $idx is outside of ... ")
        return idx
    }

    public actual data object Allocator {
        private val placement: NativeFreeablePlacement get() = nativeHeap

        public actual fun allocate(size: Int): ShortMemory = allocate(size.toLong())

        public actual fun allocate(size: Long): ShortMemory = ShortMemory(placement.allocArray(size), size)

        public actual fun release(chunk: ShortMemory) {
            placement.free(chunk.pointer)
        }
    }
}