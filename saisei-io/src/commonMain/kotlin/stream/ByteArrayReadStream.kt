package saisei.io.stream

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import naibu.ext.coerceAtMost
import naibu.ext.collections.arraycopy
import naibu.ext.size
import naibu.math.toIntSafe
import saisei.io.exception.EOFException
import saisei.io.memory.ByteMemory

public class ByteArrayReadStream(private val array: ByteArray) : SeekableReadStream {
    private val mx = Mutex()
    private var mutPosition by atomic(0)
    private val remaining get() = array.size - mutPosition

    override val position: Long get() = mutPosition.toLong()

    override suspend fun available(): Int = mx.withLock { remaining }

    override suspend fun discard(n: Long): Long = mx.withLock {
        val r = n
            .toIntSafe()
            .coerceAtMost(remaining)

        if (r > 0) {
            mutPosition += r
            r.toLong()
        } else {
            -1L
        }
    }

    override suspend fun discardFully(n: Long): Unit = mx.withLock {
        if (n > remaining) throw EOFException()
        mutPosition += n.toIntSafe()
    }

    override suspend fun read(): Byte = mx.withLock {
        if (mutPosition > array.lastIndex) throw EOFException()
        array[mutPosition++]
    }

    override suspend fun read(dst: ByteArray, at: IntRange): Int = mx.withLock {
        val rm = at.coerceAtMost(remaining)
        if (rm.size > 0) {
            arraycopy(array, mutPosition, dst, at.first, rm.size)
            mutPosition += rm.size
            rm.size
        } else {
            -1
        }
    }

    override suspend fun readFully(dst: ByteArray, at: IntRange): Unit = mx.withLock {
        if (at.size > remaining) throw EOFException()
        arraycopy(array, mutPosition, dst, at.first, at.size)
        mutPosition += at.size
    }

    override suspend fun readFully(dst: ByteMemory, at: LongRange): Unit = mx.withLock {
        if (at.size > remaining) throw EOFException()
        dst.store(at.first, array, mutPosition..<mutPosition + at.size.toIntSafe())
        mutPosition += at.size.toIntSafe()
    }

    override suspend fun read(dst: ByteMemory, at: LongRange): Long = mx.withLock {
        val len = at.size.coerceAtMost(remaining.toLong())
        if (len > 0) {
            dst.store(at.first, array, mutPosition..<mutPosition + len.toIntSafe())
            mutPosition += len.toIntSafe()
        }

        len
    }

    override suspend fun seek(to: Long): Unit = mx.withLock {
        if (to !in array.indices) throw EOFException()
        mutPosition = to.toIntSafe()
    }

    override fun toString(): String {
        return "ByteArrayReadStream(position=$position, remaining=$remaining)"
    }
}
