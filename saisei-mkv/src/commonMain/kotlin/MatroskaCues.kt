package saisei.container.mkv

/**
 * Information on Cue Points.
 */
sealed interface MatroskaCues {
    /**
     * The [MatroskaCuePoint]s that were found.
     */
    @JvmInline
    value class Realized(val cues: List<MatroskaCuePoint>) : MatroskaCues

    /**
     * The position of the [Segment.Cues] element, occurs whenever a [Segment.Cluster] was found before [Segment.Cues]
     * but a [Segment.SeekHead.Seek.SeekPosition] was present.
     */
    data class Reference(val segmentPosition: Long) : MatroskaCues

    /**
     * No [MatroskaCuePoint]s were found.
     */
    data object None : MatroskaCues
}
