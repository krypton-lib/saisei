package saisei.codec.opus.decoder

import naibu.common.Closeable
import naibu.math.toIntSafe
import saisei.codec.CodecParameters
import saisei.codec.opus.OpusException
import saisei.codec.opus.OpusPacketTools
import saisei.codec.opus.OpusResultCode
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import tomp2p.opuswrapper.Opus
import java.nio.IntBuffer

public actual class OpusDecoder actual constructor(public val parameters: CodecParameters) : Closeable {
    private val rp = run {
        val error = IntBuffer.allocate(1)

        /* create the opus decoder. */
        val rp = Opus.INSTANCE.opus_decoder_create(parameters.sampleRate, parameters.channelCount, error)

        // check if there was an exception
        val code = OpusResultCode.valueOf(error[0])
        if (code != OpusResultCode.Ok) throw OpusException(code.value, "$code")

        // return the pointer.
        rp
    }

    public actual fun decode(input: ByteMemorySlice, output: ShortMemorySlice): ShortMemorySlice {
        val outputBuf = output.memory.buffer.duplicate()
        outputBuf.position(output.range.first.toIntSafe())
        outputBuf.limit(output.range.last.toIntSafe() + 1)

        val result = Opus.INSTANCE.opus_decode(
            rp,
            input.copyToArray(),
            input.size.toIntSafe(),
            outputBuf,
            OpusPacketTools.getPacketSampleCount(parameters.sampleRate, input),
            0
        )

        if (result < 0) {
            val code = OpusResultCode.valueOf(result)
            throw OpusException(code.value, "$code")
        }

        return output[0..<result * parameters.channelCount]
    }

    override fun close() {
        Opus.INSTANCE.opus_decoder_destroy(rp)
    }
}