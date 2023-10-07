package saisei.io.memory

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.*
import naibu.ext.size
import platform.posix.memcpy
import saisei.io.order.ByteOrder

@OptIn(ExperimentalForeignApi::class)
public actual class ByteMemory(
    public val pointer: CPointer<ByteVar>,
    override val size: Long,
) : Memory<Byte>, SynchronizedObject() {
    override val order: ByteOrder get() = ByteOrder.Native

    override fun load(index: Long): Byte = synchronized(this) {
        pointer[index]
    }

    override fun store(index: Long, value: Byte): Unit = synchronized(this) {
        pointer[index] = value
    }

    public actual fun load(index: Long, dst: ByteArray, at: IntRange): Unit = synchronized(this) {
        dst.usePinned {
            val self = pointer + checkBoundsL(index, at.size)
            memcpy(it.addressOf(at.first), self, at.size.convert())
        }
    }

    public actual fun load(index: Long, dst: ByteMemory, at: LongRange): Unit = synchronized(this) {
        memcpy(
            dst.pointer + dst.checkBoundsI(at.first, at.size),
            pointer + checkBoundsI(index, at.size),
            at.size.convert()
        )
    }

    public actual fun store(index: Long, src: ByteArray, from: IntRange): Unit = synchronized(this) {
        src.usePinned {
            val self = pointer + checkBoundsI(index, from.size.toLong())
            memcpy(self, it.addressOf(from.first), from.size.toULong())
        }
    }

    public actual fun store(index: Long, src: ByteMemory, from: LongRange): Unit = synchronized(this) {
        src.load(from.first, this, checkBoundsI(index, from.size)..<from.size)
    }

    override fun close() {
        Allocator.release(this)
    }

    public actual fun shorts(): ShortMemory = ShortMemory(pointer.reinterpret(), size shr 1)

    @PublishedApi
    internal inline fun checkBoundsL(idx: Long, n: Int = 1): Long {
        if (size - n < idx) throw IndexOutOfBoundsException("Index $idx is outside of ... ")
        return idx
    }

    private inline fun checkBoundsI(idx: Long, n: Long = 1): Long {
        if (size - n < idx) throw IndexOutOfBoundsException("Index $idx is outside of ... ")
        return idx
    }

    public actual data object Allocator {
        private val placement: NativeFreeablePlacement get() = nativeHeap

        public actual fun allocate(size: Int): ByteMemory = allocate(size.toLong())

        public actual fun allocate(size: Long): ByteMemory = ByteMemory(placement.allocArray(size), size)

        public actual fun release(chunk: ByteMemory) {
            placement.free(chunk.pointer)
        }
    }
}