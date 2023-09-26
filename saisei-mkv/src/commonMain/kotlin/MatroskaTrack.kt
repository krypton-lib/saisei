package saisei.container.mkv

import naibu.io.memory.Memory
import saisei.container.mkv.element.Segment

data class MatroskaTrack(
    /**
     * A unique ID to identify the Track.
     */
    val id: Long,
    /**
     * The track number as used in the Block Header.
     */
    val num: Long,
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
    data class Codec(
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
        val delay: Long?,
        /**
         * Private data only known to the codec.
         */
        val privateData: Memory?,
    )

    data class Audio(
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
}
