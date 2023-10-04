package saisei.container.mkv

/**
 *
 */
data class MatroskaCuePoint(
    /**
     * Timecode using the file timescale
     */
    val timecode: Long,
    /**
     * Contain positions for different tracks corresponding to the timestamp.
     */
    val offsets: List<Offset>,
) {
    data class Offset(
        /**
         * The track for which a position is given.
         */
        val trackNumber: Long,
        /**
         * The Segment Position (segment-position) of the Cluster containing the associated Block.
         */
        val trackClusterOffset: Long,
    )
}
