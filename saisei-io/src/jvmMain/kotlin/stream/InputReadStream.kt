package saisei.io.stream

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.withContext
import naibu.ext.asByte
import naibu.ext.size
import naibu.math.toIntSafe
import saisei.io.exception.EOFException
import saisei.io.memory.ByteMemory
import java.io.InputStream
import kotlin.coroutines.coroutineContext

public class InputReadStream(private val stream: InputStream) : SeekableReadStream, AbstractReadStream() {
    private var mutablePosition by atomic(0L)
    override val position: Long get() = mutablePosition

    override suspend fun available(): Int = withContext(coroutineContext) {
        stream.available()
    }

    override suspend fun read(): Byte = withContext(coroutineContext) {
        throwOnEof { stream.read() }
            .asByte()
            .also { mutablePosition++ }
    }

    override suspend fun read(dst: ByteArray, at: IntRange): Int = withContext(coroutineContext) {
        throwOnEof { stream.read(dst, at.first, at.size) }
            .also { mutablePosition += it.toLong() }
    }

    override suspend fun read(dst: ByteMemory, at: LongRange): Long {
        val buf = ByteArray(at.size.toIntSafe())

        val read = read(buf)
        dst.store(at.first, buf, 0..<read)

        return read.toLong()
    }

    override suspend fun discard(n: Long): Long = withContext(coroutineContext) {
        stream.skip(n).also { mutablePosition += it }
    }

    override fun close() {
        stream.close()
    }

    private fun throwOnEof(block: InputStream.() -> Int): Int {
        val res = block(stream)
        if (res == -1) throw EOFException("No more bytes to read.")

        return res
    }

    override suspend fun seek(to: Long) {
        if (position < to) TODO()
        discardFully(to - position)
    }
}