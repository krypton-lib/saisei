package saisei.codec.opus.encoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusResultCode
import saisei.codec.opus.opus
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusEncoder(
    public val parameters: CodecParameters,
    config: OpusEncoderConfig,
) : Closeable {
    private val inner = opus.encoder_create(parameters.sampleRate, parameters.channelCount, config.application.value)

    init {
        // initialize with the given config.
        for (option in config.options) {
            val result = inner.ctl(option.request, option.value)
                .let(OpusResultCode.Companion::valueOf)
            if (result != OpusResultCode.Ok) {
                throw OpusException(result.value, "Failed to set encoder control value $option")
            }
        }
    }

    /**
     * Encodes the provided [pcm] and writes it to [opus]
     *
     * @param input  The 16-bit pcm array
     * @param output Output UByteArray for encoded opus
     * @return The resized [output].
     */
    public fun encode(
        input: ShortMemorySlice,
        output: ByteMemorySlice,
    ): ByteMemorySlice {
        val result = inner.encode(input, parameters.chunkSampleCount, output)
        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "Failed to encode pcm to opus: $code")
        }

        return output[0..<result]
    }

    override fun close() {
        inner.destroy()
    }
}