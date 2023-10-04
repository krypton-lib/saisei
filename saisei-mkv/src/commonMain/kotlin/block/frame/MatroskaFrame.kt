package saisei.container.mkv.block.frame

import naibu.io.memory.Memory
import naibu.io.slice.MemorySlice
import naibu.io.slice.get

data class MatroskaFrame(
    /**
     * The buffer that this frame is stored in.
     */
    val buffer: Memory,
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
    fun asSlice(): MemorySlice = buffer[range]
}
