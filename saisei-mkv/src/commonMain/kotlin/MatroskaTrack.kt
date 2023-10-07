package saisei.container.mkv

import naibu.math.toIntSafe
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.mustBe
import saisei.io.memory.ByteMemory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

public data class MatroskaTrack(
    /**
     * A unique ID to identify the Track.
     */
    val id: Long,
    /**
     * The track number as used in the Block Header.
     */
    val number: Long,
    /**
     * The type of this Track.
     */
    val type: Segment.Tracks.TrackEntry.TrackType,
    /**
     * Codec Information
     */
    val codec: Codec,
    /**
     * Audio Settings
     */
    val audio: Audio? = null,
) {
    public data class Codec(
        /**
         * An ID corresponding to the codec, see [Matroska codec RFC](https://www.matroska.org/technical/codec_specs.html) for more info.
         */
        val id: String,
        /**
         * The codec-built-in delay, expressed in Matroska Ticks -- i.e., in nanoseconds; see timestamp-ticks. It
         * represents the amount of codec samples that will be discarded by the decoder during playback.
         *
         * This timestamp value **MUST** be subtracted from each frame timestamp in order to get the timestamp that
         * will be actually played. The value **SHOULD** be small so the muxing of tracks with the same actual timestamp
         * are in the same Cluster.
         */
        val delay: Duration,
        /**
         * Private data only known to the codec.
         */
        val privateData: ByteMemory?,
    )

    public data class Audio(
        /**
         * The sampling frequency in Hz.
         */
        val samplingFrequency: Double,
        /**
         * The real output sampling frequency in Hz (used for SBR techniques).
         */
        val outputSamplingFrequency: Double?,
        /**
         * The number of channels in the track.
         */
        val channelCount: Int,
        /**
         * The number of bits per sample, mostly for PCM.
         */
        val bitDepth: Int?,
    )

    public companion object {
        /*
         * TODO:
         * Since Tracks have a decent number of fields maybe it would be better to read only the ones we need?
         */

        public suspend fun MasterElement.readMatroskaTracks(): List<MatroskaTrack> {
            this mustBe Segment.Tracks

            return consumeFully()
                .children(Segment.Tracks.TrackEntry)
                .map { it.readMatroskaTrack() }
        }

        public suspend fun MasterElement.readMatroskaTrack(): MatroskaTrack {
            this mustBe Segment.Tracks.TrackEntry

            val codec = Codec(
                child(Segment.Tracks.TrackEntry.CodecID).read(),
                child(Segment.Tracks.TrackEntry.CodecDelay).read().nanoseconds,
                childOrNull(Segment.Tracks.TrackEntry.CodecPrivate)?.read()
            )

            val audio = childOrNull(Segment.Tracks.TrackEntry.Audio)?.let { audio ->
                Audio(
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
    }
}
