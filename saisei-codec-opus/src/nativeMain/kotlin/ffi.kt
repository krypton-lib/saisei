package saisei.codec.opus

import kotlinx.cinterop.*
import naibu.math.toIntSafe
import saisei.codec.opus.ffi.*
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.asPointer

@OptIn(ExperimentalForeignApi::class)
internal actual object opus {
    actual fun decoder_create(Fs: Int, channels: Int): opus_decoder {
        val ptr = memScoped {
            val error = allocArray<IntVar>(1)
            val enc = opus_decoder_create(Fs, channels, error)
            val code = OpusResultCode.valueOf(error[0])
            if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder $code")
            enc
        }

        return object : opus_decoder {
            override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int =
                opus_decode(
                    ptr,
                    data.asPointer?.reinterpret(),
                    data.size.toIntSafe(),
                    pcm.asPointer,
                    pcm.size.toIntSafe() / channels,
                    0
                )

            override fun destroy(): Unit = opus_decoder_destroy(ptr)
        }
    }

    actual fun encoder_create(
        Fs: Int,
        channels: Int,
        application: Int,
    ): opus_encoder {
        val enc = memScoped {
            val error = allocArray<IntVar>(1)
            val enc = opus_encoder_create(Fs, channels, application, error)
            val code = OpusResultCode.valueOf(error[0])
            if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder $code")
            enc
        }

        return object : opus_encoder {
            override fun ctl(request: Int, value: Int): Int = opus_encoder_ctl(enc, request, value)

            override fun encode(pcm: ShortMemorySlice, frame_size: Int, data: ByteMemorySlice): Int =
                opus_encode(
                    enc,
                    pcm.asPointer,
                    frame_size,
                    data.asPointer?.reinterpret(),
                    data.size.toIntSafe()
                )

            override fun destroy() = opus_encoder_destroy(enc)
        }
    }
}