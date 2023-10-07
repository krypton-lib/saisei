package saisei.io.memory

import naibu.common.Closeable
import naibu.math.toIntSafe
import saisei.io.order.ByteOrder

public interface Memory<Item> : Closeable {
    /**
     * The indices that are available to be accessed in this chunk.
     */
    public val indices: LongRange get() = 0..<size

    /**
     * The size of this chunk.
     */
    public val size: Long

    public val size32: Int get() = size.toIntSafe()

    /**
     * The byte order.
     */
    public val order: ByteOrder

    /**
     * Reads a byte at the specified [index]
     */
    public fun load(index: Long): Item

    /**
     * Stores [value] at the specified [index]
     */
    public fun store(index: Long, value: Item)
}
