package saisei.container.mkv

/**
 * Matroska file cue point. Provides the offsets at a specific timecode for each track
 *
 * @param timecode            Timecode using the file timescale
 * @param trackClusterOffsets Absolute offset to the cluster
 */
data class MatroskaCuePoint(
    /**
     * Timecode using the file timescale
     */
    val timecode: Long,
    /**
     * The ID of the track this cue point belongs to.
     */
    val trackId: Long,
    /**
     * Absolute offset to the cluster
     */
    val trackClusterOffsets: LongArray,
)
