package saisei.container.mkv

import naibu.ext.into
import saisei.container.mkv.MatroskaCuePoint.Companion.readMatroskaCuePoint
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.MasterElement
import saisei.io.format.ebml.element.children
import saisei.io.format.ebml.element.consumeFully
import saisei.io.format.ebml.mustBe
import saisei.io.stream.SeekableReadStream
import kotlin.jvm.JvmInline

/**
 * Information on Cue Points.
 */
public sealed interface MatroskaCues {
    /**
     * The [MatroskaCuePoint]s that were found.
     */
    @JvmInline
    public value class Found(public val value: List<MatroskaCuePoint>) : MatroskaCues {
        /**
         * Finds the offset of the closest timecode to [timecode].
         *
         * @param trackNumber The track number
         * @param timecode    The timecode
         */
        public fun findCluster(trackNumber: Long, timecode: Long): Long? = value.find { it.timecode > timecode }
            ?.offsets
            ?.find { it.trackNumber == trackNumber }
            ?.trackClusterOffset
    }

    /**
     * The position of the [Segment.Cues] element, occurs whenever a [Segment.Cluster] was found before [Segment.Cues]
     * but a [Segment.SeekHead.Seek.SeekPosition] was present.
     */
    public data class Reference(val segmentPosition: Long) : MatroskaCues

    /**
     * No [MatroskaCuePoint]s were found.
     */
    public data object None : MatroskaCues

    public companion object {
        public suspend fun MasterElement.readMatroskaCues(): MatroskaCues {
            this mustBe Segment.Cues

            val cuePoints = consumeFully()
                .children(Segment.Cues.CuePoint)
                .map { it.readMatroskaCuePoint() }

            return if (cuePoints.isEmpty()) None else Found(cuePoints)
        }

        /**
         * Attempts to find [cues][Found] inside of this segment, if only a reference to their position is known then
         * the stream will be seeked to it, so they can be read.
         *
         * @param stream The stream to use if only a reference is known.
         * @return The cues that were found, or `null` if they are not present.
         */
        public suspend fun MatroskaSegment.findCues(stream: SeekableReadStream): Found? =
            when (cues) {
                None -> null
                is Found -> cues
                is Reference -> {
                    // seek to the cue point.
                    stream.seek(header.dataPosition + cues.segmentPosition)

                    // read the cues element
                    Segment.Cues.consumeFully(stream)
                        .readMatroskaCues()
                        .into<Found>()
                }
            }
    }
}
