package saisei.codec.opus

import club.minnced.opus.util.OpusLibrary
import naibu.math.toIntSafe
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.resizedBuffer
import tomp2p.opuswrapper.Opus
import java.nio.IntBuffer

// TODO: custom jni bindings

internal actual object opus {
    actual fun decoder_create(Fs: Int, channels: Int): opus_decoder {
        OpusLibrary.loadFromJar()

        val error = IntBuffer.allocate(1)
        val ptr = Opus.INSTANCE.opus_decoder_create(Fs, channels, error)
        val code = OpusResultCode.valueOf(error[0])
        if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create decoder: $code")

        return object : opus_decoder {
            override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int =
                Opus.INSTANCE.opus_decode(
                    ptr,
                    data.copyToArray(),
                    data.size.toIntSafe(),
                    pcm.resizedBuffer(),
                    pcm.size.toIntSafe() / channels,
                    0
                )

            override fun destroy() = Opus.INSTANCE.opus_decoder_destroy(ptr)
        }
    }

    actual fun encoder_create(
        Fs: Int,
        channels: Int,
        application: Int,
    ): opus_encoder {
        OpusLibrary.loadFromJar()

        val error = IntBuffer.allocate(1)
        val ptr = Opus.INSTANCE.opus_encoder_create(Fs, channels, application, error)
        val code = OpusResultCode.valueOf(error[0])
        if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder: $code")

        return object : opus_encoder {
            override fun ctl(request: Int, value: Int): Int =
                Opus.INSTANCE.opus_encoder_ctl(ptr, request, value)

            override fun encode(pcm: ShortMemorySlice, frame_size: Int, data: ByteMemorySlice): Int =
                Opus.INSTANCE.opus_encode(
                    ptr,
                    pcm.resizedBuffer(),
                    frame_size,
                    data.resizedBuffer(),
                    data.size.toIntSafe()
                )

            override fun destroy() = Opus.INSTANCE.opus_encoder_destroy(ptr)
        }
    }

}