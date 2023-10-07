package saisei.io.stream

import naibu.common.pool.use
import naibu.ext.offset
import naibu.ext.size
import saisei.io.exception.EOFException
import saisei.io.memory.ByteMemory
import saisei.io.memory.SmallByteMemoryPool

public abstract class AbstractReadStream : ReadStream {
    override suspend fun available(): Int {
        return 0
    }

    override suspend fun read(): Byte = SmallByteMemoryPool.use {
        if (read(it, 0..1L) != 1L) throw EOFException()
        it.load(0)
    }

    override suspend fun readFully(dst: ByteMemory, at: LongRange) {
        var completed = 0L
        while (completed < at.size) {
            val read = read(dst, at offset completed)
            if (read < 0) throw EOFException()
            completed += read
        }
    }

    override suspend fun readFully(dst: ByteArray, at: IntRange) {
        var completed = 0
        while (completed < at.size) {
            val read = read(dst, at offset completed)
            if (read < 0) throw EOFException()
            completed += read
        }
    }

    override suspend fun discard(n: Long): Long {
        if (n <= 0) {
            return 0
        }

        val discardBuf = ByteArray(n.coerceAtMost(2048).toInt())
        var progress = 0L
        while (progress < n) {
            val skipped = read(discardBuf)
            if (skipped < 0) break
            progress += skipped
        }

        return progress
    }

    override suspend fun discardFully(n: Long) {
        var target = n
        while (target > 0) target -= discard(target)
    }
}
