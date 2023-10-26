package saisei.codec.opus.encoder

import naibu.common.Closeable
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusResultCode
import saisei.codec.opus.opus
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusEncoder(
    public val parameters: CodecParameters,
    config: OpusEncoderConfig,
) : Closeable, CodecChunkEncoder {
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
     * @param pcm  The 16-bit pcm array
     * @param data Output UByteArray for encoded opus
     * @return The resized [data].
     */
    override fun encode(
        pcm: ShortMemorySlice,
        data: ByteMemorySlice,
    ): ByteMemorySlice {
        val result = inner.encode(pcm, parameters.frameSampleCount, data)
        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "Failed to encode pcm to opus: $code")
        }

        return data[0..<result]
    }

    override fun close() {
        inner.destroy()
    }
}