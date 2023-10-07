package saisei.io.stream

import naibu.common.Closeable
import naibu.common.Resetable
import saisei.io.memory.ByteMemory

public interface ReadStream : Closeable, Resetable {
    /**
     * @return The number of bytes that can be read.
     */
    public suspend fun available(): Int

    /**
     * Read at-most n bytes (size of [at]) from this stream, suspending until available.
     *
     * @param dst The destination of the read bytes.
     * @param at  Where in [dst] to write the bytes.
     * @return The number of bytes that were read or -1 if the end-of-stream was reached.
     */
    public suspend fun read(dst: ByteMemory, at: LongRange = dst.indices): Long

    /**
     * Read at-most n bytes (size of [at]) from this stream, suspending until available.
     *
     * @param dst The destination of the read bytes.
     * @param at  Where in [dst] to write the bytes.
     * @return The number of bytes that were read or -1 if the end-of-stream was reached.
     */
    public suspend fun read(dst: ByteArray, at: IntRange = dst.indices): Int

    /**
     * Read a single byte from the stream, suspending until available.
     *
     * @return The byte that was read.
     * @throws saisei.io.exception.EOFException If the stream has been closed.
     */
    public suspend fun read(): Byte

    /**
     * Read fully
     *
     * @param dst The destination of the read bytes.
     * @param at  Where in [dst] to write the bytes.
     * @throws saisei.io.exception.EOFException If the stream has been closed.
     */
    public suspend fun readFully(dst: ByteArray, at: IntRange = dst.indices)

    /**
     * Read fully
     *
     * @param dst The destination of the read bytes.
     * @param at  Where in [dst] to write the bytes.
     * @throws saisei.io.exception.EOFException If the stream has been closed.
     */
    public suspend fun readFully(dst: ByteMemory, at: LongRange = dst.indices)

    /**
     * Discards at-most [n] bytes from this stream, use [discardFully] if you want all [n]
     * bytes to be discarded.
     *
     * @param n The maximum number of bytes to discard.
     * @return The number of bytes that were discarded.
     * @throws saisei.io.exception.EOFException If the stream has been closed.
     */
    public suspend fun discard(n: Long): Long

    /**
     * Discards [n] bytes from this stream, suspending until completed.
     *
     * @param n The number of bytes to discard.
     * @throws saisei.io.exception.EOFException If the stream has been closed.
     */
    public suspend fun discardFully(n: Long)

    // DEFAULT IMPLEMENTATIONS

    override fun close() {
    }

    override fun reset() {
    }
}
