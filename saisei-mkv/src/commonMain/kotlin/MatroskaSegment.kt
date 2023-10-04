package saisei.container.mkv

import saisei.io.format.ebml.element.ElementHeader
import saisei.io.format.ebml.element.MasterElement
import kotlin.time.Duration

data class MatroskaSegment(
    /**
     * The header of this element.
     */
    val header: ElementHeader,
    /**
     * Information on this Segment.
     */
    val info: Info,
    /**
     * The cue points that were found.
     */
    val cues: MatroskaCues,
    /**
     * The tracks that are contained within this MKV file.
     */
    val tracks: List<MatroskaTrack>,
    /**
     * The tags that were contained in this Segment.
     */
    val tags: List<MatroskaTag>,
    /**
     * The first cluster element that was encountered.
     */
    val firstCluster: MasterElement,
) {
    fun convertTimecodeToDuration(timecode: Long): Duration = ticksToDuration(info.timestampScale, timecode)

    data class Info(
        /**
         * Base unit for Segment Ticks and Track Ticks, in nanoseconds. A TimestampScale value of 1000000 means scaled
         * timestamps in the Segment are expressed in milliseconds; see timestamps on how to interpret timestamps.
         */
        val timestampScale: Long,
        /**
         * The duration
         */
        val duration: Duration?,
    )
}
