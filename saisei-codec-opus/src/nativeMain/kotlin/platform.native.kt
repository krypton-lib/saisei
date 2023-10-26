package saisei.codec.opus

import kotlinx.cinterop.*
import naibu.math.toIntSafe
import saisei.codec.opus.ffi.*
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.asPointer

internal actual fun getOpusLibrary(): NativeOpusLibrary = Opus

@OptIn(ExperimentalForeignApi::class)
private object Opus : NativeOpusLibrary {
    override fun createDecoder(sampleRate: Int, channels: Int): NativeOpusDecoder {
        val ptr = memScoped {
            val error = allocArray<IntVar>(1)
            val enc = opus_decoder_create(sampleRate, channels, error)
            val code = OpusResultCode.valueOf(error[0])
            if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder $code")
            enc
        }

        return object : NativeOpusDecoder {
            override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int =
                opus_decode(
                    ptr,
                    data.asPointer?.reinterpret(),
                    data.size.toIntSafe(),
                    pcm.asPointer,
                    pcm.size.toIntSafe() / channels,
                    0
                )

            override fun close(): Unit = opus_decoder_destroy(ptr)
        }
    }

    override fun createEncoder(
        sampleRate: Int,
        channels: Int,
        application: Int,
    ): NativeOpusEncoder {
        val enc = memScoped {
            val error = allocArray<IntVar>(1)
            val enc = opus_encoder_create(sampleRate, channels, application, error)
            val code = OpusResultCode.valueOf(error[0])
            if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder $code")
            enc
        }

        return object : NativeOpusEncoder {
            override val supportsCTL: Boolean get() = true

            override fun ctl(request: Int, value: Int): Int = opus_encoder_ctl(enc, request, value)

            override fun encode(pcm: ShortMemorySlice, frameSize: Int, data: ByteMemorySlice): Int =
                opus_encode(
                    enc,
                    pcm.asPointer,
                    frameSize,
                    data.asPointer?.reinterpret(),
                    data.size.toIntSafe()
                )

            override fun close() = opus_encoder_destroy(enc)
        }
    }
}