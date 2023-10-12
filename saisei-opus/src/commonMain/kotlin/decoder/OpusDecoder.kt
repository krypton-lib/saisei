package saisei.codec.opus.decoder

import naibu.common.Closeable
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusResultCode
import saisei.codec.opus.opus
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusDecoder(public val parameters: CodecParameters) : Closeable, CodecChunkDecoder {
    private val inner = opus.decoder_create(parameters.sampleRate, parameters.channelCount)

    /**
     * Encodes the provided [pcm] and writes it to [data]
     *
     * @param data Encoded opus buffer to decode.
     * @param pcm  The 16-bit pcm output buffer
     * @return The resized [pcm] buffer that contains the decoded samples.
     */
    public override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice {
        val result = inner.decode(data, pcm)
        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "$code")
        }

        return pcm[0..<result * parameters.channelCount]
    }

    override fun close() {
        inner.destroy()
    }
}