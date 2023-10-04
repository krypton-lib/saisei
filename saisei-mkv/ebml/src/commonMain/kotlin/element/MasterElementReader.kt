package saisei.io.format.ebml.element

import naibu.common.Closeable

interface MasterElementReader : Closeable {
    val header: ElementHeader

    /**
     * The number of remaining bytes to be read.
     */
    val remaining: Long

    val hasRemaining: Boolean get() = remaining > 0

    /**
     * Whether this reader has been closed.
     */
    val closed: Boolean

    /**
     * A list of [Child Element][Element]s that have been read so far.
     */
    val children: List<Element>

    /**
     *
     */
    suspend fun readNextChild(): Element?

    /**
     * Skips the last read child.
     */
    suspend fun skipLastChild()

    /**
     *
     */
    fun self(): MasterElement
}