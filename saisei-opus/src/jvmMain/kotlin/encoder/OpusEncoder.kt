package saisei.codec.opus.encoder

import naibu.common.Closeable
import naibu.math.toIntSafe
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusResultCode
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.resizedBuffer
import tomp2p.opuswrapper.Opus
import java.nio.IntBuffer

public actual class OpusEncoder actual constructor(
    public val parameters: CodecParameters,
    public val config: OpusEncoderConfig,
) : Closeable {
    private val rp = run {
        val error = IntBuffer.allocate(1)

        /* create the opus decoder. */
        val rp = Opus.INSTANCE.opus_encoder_create(
            parameters.sampleRate,
            parameters.channelCount,
            config.application.value,
            error
        )

        // check if there was an exception
        val code = OpusResultCode.valueOf(error[0])
        if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder $code")

        // initialize with the given config.
        for (option in config.options) {
            val result = Opus.INSTANCE
                .opus_encoder_ctl(rp, option.request, option.value)
                .let(OpusResultCode.Companion::valueOf)

            if (result != OpusResultCode.Ok) {
                throw OpusException(result.value, "Failed to set encoder control value $option")
            }
        }

        // return the pointer.
        rp
    }

    public actual fun encode(input: ShortMemorySlice, output: ByteMemorySlice): ByteMemorySlice {
        val result = Opus.INSTANCE.opus_encode(
            rp,
            input.resizedBuffer(),
            parameters.chunkSampleCount,
            output.resizedBuffer(),
            output.size.toIntSafe()
        )

        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "Failed to encode pcm to opus: $code")
        }

        return output[0..<result]
    }

    override fun close() {
    }
}