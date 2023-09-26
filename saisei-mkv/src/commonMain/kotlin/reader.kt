package saisei.container.mkv

import naibu.cio.stream.read.SeekableReadStream
import naibu.math.toIntSafe
import naibu.monads.Result
import naibu.monads.err
import naibu.monads.ok
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.EBML
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.into
import saisei.io.format.ebml.matches
import saisei.io.format.ebml.mustBe

sealed class MatroskaFileException : Exception {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable?) : super(message, cause)

    class NotPlayable(message: String) : MatroskaFileException(message)

    class ReadingFailed(message: String, cause: Throwable? = null) : MatroskaFileException(message, cause)

    class NotMatroska(message: String = "File is not Matroska") : MatroskaFileException(message)
}

/**
 * Reads a Matroska File. This will read up until the first occurrence of a [Segment.Cluster]
 * element.
 */
suspend fun readMatroskaFile(stream: SeekableReadStream): Result<MatroskaFile, MatroskaFileException> {
    try {
        /* check if the EBML doc-type matches webm/matroska */
        val docType = try {
            readDocType(stream)
        } catch (ex: EBMLException.UnexpectedElement) {
            if (ex.expected matches EBML.id) {
                // not a matroska file.
                return MatroskaFileException.NotMatroska("MKV file is missing EBML header element.").err()
            }

            return MatroskaFileException.ReadingFailed("Failed to read EBML doc-type").err()
        }

        if (docType !in listOf("webm", "matroska")) {
            return MatroskaFileException.NotMatroska("EBML DocType is not webm/matroska").err()
        }

        /* start reading the first Segment element */
        Segment.consume(stream, discardRemaining = false) {
            var cues: MatroskaCues = MatroskaCues.None
            var tracks: List<MatroskaTrack> = emptyList()
            var firstClusterElement: MasterElement? = null
            readUntilEOF { child ->
                var cont = true
                when {
                    child matches Segment.Cues -> {
                        cues = child.into(Segment.Cues)!!.readMatroskaCues()
                    }

                    child matches Segment.Tracks -> {
                        tracks = child.into(Segment.Tracks)!!.readMatroskaTracks()
                    }

                    child matches Segment.SeekHead -> {
                        val seekHead = child.into(Segment.SeekHead)!!
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
                        firstClusterElement = child.into(Segment.Cluster)
                        cont = false
                    }

                    else -> skipLastChild()
                }

                cont
            }

            return when {
                tracks.isEmpty() ->
                    MatroskaFileException.NotPlayable("MKV file contains no tracks.").err()

                firstClusterElement == null ->
                    MatroskaFileException.NotPlayable("MKV file does not contain a Cluster element").err()

                else -> MatroskaFile(cues, tracks, firstClusterElement!!).ok()
            }
        }
    } catch (ex: Throwable) {
        return MatroskaFileException.ReadingFailed("Failed to read MKV elements", ex).err()
    }
}

suspend fun readDocType(stream: SeekableReadStream): String? =
    EBML.firstNotNullChildOrNull(stream) { it.into(EBML.DocType)?.read() }

suspend fun MasterElement.readMatroskaCuePoint(): MatroskaCuePoint {
    this mustBe Segment.Cues.CuePoint

    val cueTrackPositions = child(Segment.Cues.CuePoint.CueTrackPositions)
    return MatroskaCuePoint(
        child(Segment.Cues.CuePoint.CueTime)
            .read(),
        cueTrackPositions
            .child(Segment.Cues.CuePoint.CueTrackPositions.CueTrack)
            .read(),
        cueTrackPositions
            .children(Segment.Cues.CuePoint.CueTrackPositions.CueClusterPosition)
            .map { it.read() }
            .toLongArray()
    )
}

suspend fun MasterElement.readMatroskaCues(): MatroskaCues {
    this mustBe Segment.Cues

    val cuePoints = consumeFully()
        .children(Segment.Cues.CuePoint)
        .map { it.readMatroskaCuePoint() }

    return if (cuePoints.isEmpty()) MatroskaCues.None else MatroskaCues.Realized(cuePoints)
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
        childOrNull(Segment.Tracks.TrackEntry.CodecDelay)?.read(),
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
