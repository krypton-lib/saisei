package saisei.io.format.ebml.element

import naibu.common.Closeable

public interface MasterElementReader : Closeable {
    public val header: ElementHeader

    /**
     * The number of remaining bytes to be read.
     */
    public val remaining: Long

    public val hasRemaining: Boolean get() = remaining > 0

    /**
     * Whether this reader has been closed.
     */
    public val closed: Boolean

    /**
     * A list of [Child Element][Element]s that have been read so far.
     */
    public val children: List<Element>

    /**
     *
     */
    public suspend fun readNextChild(): Element?

    /**
     * Skips the last read child.
     */
    public suspend fun skipLastChild()

    /**
     *
     */
    public fun self(): MasterElement
}