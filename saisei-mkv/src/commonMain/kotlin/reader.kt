package saisei.container.mkv

import naibu.cio.stream.read.SeekableReadStream
import naibu.math.toIntSafe
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.*
import saisei.io.format.ebml.element.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

sealed class MatroskaFileException : Exception {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable?) : super(message, cause)

    class NotPlayable(message: String) : MatroskaFileException(message)

    class ReadingFailed(message: String, cause: Throwable? = null) : MatroskaFileException(message, cause)

    class NotMatroska(message: String = "File is not Matroska") : MatroskaFileException(message)
}

suspend fun readMatroskaDocType(stream: SeekableReadStream) {
    val docType = try {
        readDocType(stream)
    } catch (ex: EBMLException.UnexpectedElement) {
        if (ex.expected matches EBML.id) {
            // not a matroska file.
            throw MatroskaFileException.NotMatroska("MKV file is missing EBML header element.")
        }

        throw MatroskaFileException.ReadingFailed("Failed to read EBML doc-type")
    }

    if (docType !in listOf("webm", "matroska")) {
        throw MatroskaFileException.NotMatroska("EBML DocType is not webm/matroska")
    }
}

/**
 * Reads a [MatroskaSegment] instance from the given [read stream][stream], it reads up until the first occurrence
 * of a [Segment.Cluster] element ([MatroskaSegment.firstCluster]).
 *
 * @param stream The stream to read the segment from.
 */
suspend fun readMatroskaSegment(stream: SeekableReadStream): MatroskaSegment = try {
    /* start reading the first Segment element */
    Segment.consume(stream, discardRemaining = false) {
        var cues: MatroskaCues = MatroskaCues.None
        var tracks: List<MatroskaTrack> = emptyList()
        var firstClusterElement: MasterElement? = null
        var duration: Duration? = null
        var timestampScale: Long = 1_000_000
        val tags = mutableListOf<MatroskaTag>()
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
                    child.into(Segment.Tags)
                        .consumeFully()
                        .children(Segment.Tags.Tag)
                        .map { tag ->
                            val target = tag.childOrNull(Segment.Tags.Tag.Targets)?.let { targets ->
                                targets.childOrNull(Segment.Tags.Tag.Targets.TagTrackUID)?.read()
                                    ?.let(MatroskaTag.Target::Track)
                                    ?: targets.childOrNull(Segment.Tags.Tag.Targets.TagEditionUID)?.read()
                                        ?.let(MatroskaTag.Target::Edition)
                                    ?: targets.childOrNull(Segment.Tags.Tag.Targets.TagChapterUID)?.read()
                                        ?.let(MatroskaTag.Target::Chapter)
                                    ?: targets.childOrNull(Segment.Tags.Tag.Targets.TagAttachmentUID)?.read()
                                        ?.let(MatroskaTag.Target::Attachment)
                            } ?: MatroskaTag.Target.None

                            tags += tag.children(Segment.Tags.Tag.SimpleTag).map { st ->
                                val name = st
                                    .child(Segment.Tags.Tag.SimpleTag.TagName)
                                    .read()

                                st.childOrNull(Segment.Tags.Tag.SimpleTag.TagString)
                                    ?.let { MatroskaTag.STRING(name, target, it.read()) }
                                    ?: st.childOrNull(Segment.Tags.Tag.SimpleTag.TagBinary)
                                        ?.let { MatroskaTag.BINARY(name, target, it.read()) }
                                    ?: error("SimpleTag is missing value element (TagString, TagBinary)")
                            }
                        }
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

        when {
            tracks.isEmpty() ->
                throw MatroskaFileException.NotPlayable("MKV file contains no tracks.")

            firstClusterElement == null ->
                throw MatroskaFileException.NotPlayable("MKV file does not contain a Cluster element")

            else -> MatroskaSegment(
                header,
                MatroskaSegment.Info(timestampScale, duration),
                cues,
                tracks,
                tags,
                firstClusterElement!!
            )
        }
    }
} catch (ex: Throwable) {
    throw MatroskaFileException.ReadingFailed("Failed to read MKV elements", ex)
}

suspend fun readMatroskaFile(stream: SeekableReadStream): MatroskaSegment {
    readMatroskaDocType(stream)
    return readMatroskaSegment(stream)
}

suspend fun readDocType(stream: SeekableReadStream): String? =
    EBML.firstNotNullChildOrNull(stream) { it.intoOrNull(EBML.DocType)?.read() }

suspend fun MasterElement.readMatroskaCuePointOffset(): MatroskaCuePoint.Offset {
    this mustBe Segment.Cues.CuePoint.CueTrackPositions

    return MatroskaCuePoint.Offset(
        child(Segment.Cues.CuePoint.CueTrackPositions.CueTrack).read(),
        child(Segment.Cues.CuePoint.CueTrackPositions.CueClusterPosition).read()
    )
}

suspend fun MasterElement.readMatroskaCuePoint(): MatroskaCuePoint {
    this mustBe Segment.Cues.CuePoint

    return MatroskaCuePoint(
        child(Segment.Cues.CuePoint.CueTime).read(),
        children(Segment.Cues.CuePoint.CueTrackPositions).map { it.readMatroskaCuePointOffset() }
    )
}

suspend fun MasterElement.readMatroskaCues(): MatroskaCues {
    this mustBe Segment.Cues

    val cuePoints = consumeFully()
        .children(Segment.Cues.CuePoint)
        .map { it.readMatroskaCuePoint() }

    return if (cuePoints.isEmpty()) MatroskaCues.None else MatroskaCues.Found(cuePoints)
}

// TODO: since Tracks have a decent number of fields maybe reading only the ones we need would be wiser.
suspend fun MasterElement.readMatroskaTracks(): List<MatroskaTrack> {
    this mustBe Segment.Tracks

    return consumeFully()
        .children(Segment.Tracks.TrackEntry)
        .map { it.readMatroskaTrack() }
}

suspend fun MasterElement.readMatroskaTrack(): MatroskaTrack {
    this mustBe Segment.Tracks.TrackEntry

    val codec = MatroskaTrack.Codec(
        child(Segment.Tracks.TrackEntry.CodecID).read(),
        child(Segment.Tracks.TrackEntry.CodecDelay).read().nanoseconds,
        childOrNull(Segment.Tracks.TrackEntry.CodecPrivate)?.read()
    )

    val audio = childOrNull(Segment.Tracks.TrackEntry.Audio)?.let { audio ->
        MatroskaTrack.Audio(
            audio.child(Segment.Tracks.TrackEntry.Audio.SamplingFrequency).read(),
            audio.childOrNull(Segment.Tracks.TrackEntry.Audio.OutputSamplingFrequency)?.read(),
            audio.child(Segment.Tracks.TrackEntry.Audio.Channels).read().toIntSafe(),
            audio.childOrNull(Segment.Tracks.TrackEntry.Audio.BitDepth)?.read()?.toIntSafe(),
        )
    }

    return MatroskaTrack(
        child(Segment.Tracks.TrackEntry.TrackUID).read(),
        child(Segment.Tracks.TrackEntry.TrackNumber).read(),
        child(Segment.Tracks.TrackEntry.TrackType).read(),
        codec,
        audio
    )
}
