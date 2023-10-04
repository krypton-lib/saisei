package saisei.container.mkv

import naibu.cio.stream.read.SeekableReadStream
import saisei.container.mkv.MatroskaCues.Companion.readMatroskaCues
import saisei.container.mkv.MatroskaTag.Companion.readTags
import saisei.container.mkv.MatroskaTrack.Companion.readMatroskaTracks
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.intoOrNull
import saisei.io.format.ebml.matches
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

    companion object {
        suspend fun read(stream: SeekableReadStream): MatroskaSegment {
            /* start reading the first Segment element */
            Segment.consume(stream, discardRemaining = false) {
                var cues: MatroskaCues = MatroskaCues.None
                var tracks: List<MatroskaTrack> = emptyList()
                var firstClusterElement: MasterElement? = null
                var duration: Duration? = null
                var timestampScale: Long = 1_000_000
                var tags = emptyList<MatroskaTag>()
                readUntilEOF { child ->
                    var cont = true
                    when {
                        child matches Segment.Info -> {
                            val info = child
                                .into(Segment.Info)
                                .consumeFully()

                            timestampScale = info.childOrNull(Segment.Info.TimestampScale)
                                ?.read()
                                ?: timestampScale

                            duration = info.childOrNull(Segment.Info.Duration)
                                ?.read()
                                ?.let { ticksToDuration(timestampScale, it) }
                        }

                        child matches Segment.Tags -> {
                            tags = child.into(Segment.Tags).readTags()
                        }

                        child matches Segment.Cues -> {
                            cues = child.into(Segment.Cues).readMatroskaCues()
                        }

                        child matches Segment.Tracks -> {
                            tracks = child.into(Segment.Tracks).readMatroskaTracks()
                        }

                        child matches Segment.SeekHead -> {
                            val seekHead = child.into(Segment.SeekHead)
                                .consumeFully()
                                .children(Segment.SeekHead.Seek)
                                .associate { seek ->
                                    val id = seek
                                        .child(Segment.SeekHead.Seek.SeekID)
                                        .read().code

                                    id to seek
                                        .child(Segment.SeekHead.Seek.SeekPosition)
                                        .read()
                                }

                            /* find cues */
                            val cuesSegmentPos = seekHead[Segment.Cues.id.code]
                            if (cues == MatroskaCues.None && cuesSegmentPos != null) {
                                cues = MatroskaCues.Reference(cuesSegmentPos)
                            }
                        }

                        child matches Segment.Cluster -> {
                            firstClusterElement = child.intoOrNull(Segment.Cluster)
                            cont = false
                        }

                        else -> skipLastChild()
                    }

                    cont
                }

                return when {
                    tracks.isEmpty() ->
                        throw MatroskaFileException.NotPlayable("MKV file contains no tracks.")

                    firstClusterElement == null ->
                        throw MatroskaFileException.NotPlayable("MKV file does not contain a Cluster element")

                    else -> MatroskaSegment(
                        header,
                        Info(timestampScale, duration),
                        cues,
                        tracks,
                        tags,
                        firstClusterElement!!
                    )
                }
            }
        }
    }
}
