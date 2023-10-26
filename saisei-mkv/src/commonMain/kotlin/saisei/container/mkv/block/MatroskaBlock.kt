package saisei.container.mkv.block

public sealed interface MatroskaBlock {
    /**
     * The track number which this block is for
     */
    public val trackNumber: Long

    /**
     * The timecode of this block relative to its cluster
     */
    public val timecode: Long

    /**
     * Whether this block is a keyframe
     */
    public val isKeyFrame: Boolean

    /**
     * The number of frames in this block
     */
    public val frameCount: Int

    /**
     *
     */
    public val frameSizes: IntArray

    public data class Immutable(
        override val trackNumber: Long,
        override val timecode: Long,
        override val isKeyFrame: Boolean,
        override val frameCount: Int,
        override val frameSizes: IntArray,
    ) : MatroskaBlock
}
