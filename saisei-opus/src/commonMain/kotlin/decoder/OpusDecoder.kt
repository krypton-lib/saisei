package saisei.codec.opus.decoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusResultCode
import saisei.codec.opus.opus
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusDecoder(public val parameters: CodecParameters) : Closeable {
    private val inner = opus.decoder_create(parameters.sampleRate, parameters.channelCount)

    /**
     * Encodes the provided [output] and writes it to [input]
     *
     * @param input  Encoded opus buffer to decode.
     * @param output The 16-bit pcm output buffer
     * @return The resized [output] buffer that contains the decoded samples.
     */
    public fun decode(input: ByteMemorySlice, output: ShortMemorySlice): ShortMemorySlice {
        val result = inner.decode(input, output)
        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "$code")
        }

        return output[0..<result * parameters.channelCount]
    }

    override fun close() {
        inner.destroy()
    }
}