package saisei.container.mkv.block.frame

import saisei.io.memory.ByteMemory
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.get

public data class MatroskaFrame(
    /**
     * The buffer that this frame is stored in.
     */
    val buffer: ByteMemory,
    /**
     * Where in [buffer] this frame resides.
     */
    val range: LongRange,
    /**
     * The number of the track this frame belongs to.
     */
    val trackNumber: Long,
    /**
     * The timecode of this frame (in milliseconds).
     */
    val timecode: Long,
) {
    /**
     * Returns the slice of data this block occupies.
     */
    public fun asSlice(): ByteMemorySlice = buffer[range]
}
