package saisei.container.mkv

import saisei.container.mkv.MatroskaCuePoint.Offset.Companion.readMatroskaCuePointOffset
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.MasterElement
import saisei.io.format.ebml.element.child
import saisei.io.format.ebml.element.children
import saisei.io.format.ebml.mustBe

/**
 *
 */
public data class MatroskaCuePoint(
    /**
     * Timecode using the file timescale
     */
    val timecode: Long,
    /**
     * Contain positions for different tracks corresponding to the timestamp.
     */
    val offsets: List<Offset>,
) {
    public data class Offset(
        /**
         * The track for which a position is given.
         */
        val trackNumber: Long,
        /**
         * The Segment Position (segment-position) of the Cluster containing the associated Block.
         */
        val trackClusterOffset: Long,
    ) {
        public companion object {
            /**
             *
             */
            public suspend fun MasterElement.readMatroskaCuePointOffset(): Offset {
                this mustBe Segment.Cues.CuePoint.CueTrackPositions

                return Offset(
                    child(Segment.Cues.CuePoint.CueTrackPositions.CueTrack).read(),
                    child(Segment.Cues.CuePoint.CueTrackPositions.CueClusterPosition).read()
                )
            }
        }
    }

    public companion object {
        /**
         *
         */
        public suspend fun MasterElement.readMatroskaCuePoint(): MatroskaCuePoint {
            this mustBe Segment.Cues.CuePoint

            return MatroskaCuePoint(
                child(Segment.Cues.CuePoint.CueTime).read(),
                children(Segment.Cues.CuePoint.CueTrackPositions).map { it.readMatroskaCuePointOffset() }
            )
        }
    }
}
