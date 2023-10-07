package saisei.codec

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
     * The number of samples.
     */
    val chunkSampleCount: Int,
    /**
     * The type of codec.
     */
    val type: Codec,
) {
    /**
     *
     */
    val totalSampleCount: Int get() = chunkSampleCount * channelCount
}