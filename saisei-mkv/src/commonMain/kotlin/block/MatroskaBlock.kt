package saisei.container.mkv.block

sealed interface MatroskaBlock {
    /**
     * The track number which this block is for
     */
    val trackNumber: Long

    /**
     * The timecode of this block relative to its cluster
     */
    val timecode: Long

    /**
     * Whether this block is a keyframe
     */
    val isKeyFrame: Boolean

    /**
     * The number of frames in this block
     */
    val frameCount: Int

    /**
     *
     */
    val frameSizes: IntArray

    data class Immutable(
        override val trackNumber: Long,
        override val timecode: Long,
        override val isKeyFrame: Boolean,
        override val frameCount: Int,
        override val frameSizes: IntArray,
    ) : MatroskaBlock
}
