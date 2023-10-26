package saisei.io.slice.cursor

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import naibu.common.Resetable
import saisei.io.exception.EOFException
import saisei.io.slice.ByteSlice

public abstract class AbstractByteCursor(public val slice: ByteSlice) : Resetable, SynchronizedObject() {
    private var mutablePosition: Long = 0

    /**
     * The current position of the cursor.
     */
    public val position: Long
        get() = synchronized(this) { mutablePosition }

    /**
     * The number of bytes remaining before becoming exhausted
     */
    public val remaining: Long
        get() = slice.size - position

    /**
     * Whether this cursor has been exhausted (no more bytes are available).
     */
    public val isExhausted: Boolean
        get() = remaining <= 0


    override fun reset() {
        mutablePosition = 0
    }

    protected fun get(action: String, n: Long): ByteSlice = synchronized(this) {
        val pos = nextIndex(action, n)
        slice[pos..<(pos + n)]
    }

    protected fun <T> get(action: String, n: Long, block: ByteSlice.() -> T): T = get(action, n).block()

    protected fun nextIndex(action: String, n: Long = 1): Long = synchronized(this) {
        val pos = mutablePosition
        if (pos + n > slice.size) {
            throw EOFException("Cannot $action $n byte(s) @ $pos (${slice.size}).")
        }

        mutablePosition += n
        pos
    }
}
