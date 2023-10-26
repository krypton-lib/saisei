package saisei.codec

// TODO: refactor

public data class CodecParameters(
    /**
     * The sample rate.
     */
    val sampleRate: Int,
    /**
     * The number of channels.
     */
    val channelCount: Int,
    /**
     * The number of samples in a single frame.
     */
    val frameSampleCount: Int,
    /**
     * The type of codec.
     */
    val type: Codec,
) {
    /**
     *
     */
    val totalSampleCount: Int get() = frameSampleCount * channelCount
}