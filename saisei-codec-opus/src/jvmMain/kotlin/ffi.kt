package saisei.codec.opus

// TODO: custom jni bindings

//internal actual object opus {
//    actual fun decoder_create(Fs: Int, channels: Int): NativeOpusDecoder {
//        OpusLibrary.loadFromJar()
//
//        val error = IntBuffer.allocate(1)
//        val ptr = Opus.INSTANCE.opus_decoder_create(Fs, channels, error)
//        val code = OpusResultCode.valueOf(error[0])
//        if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create decoder: $code")
//
//        return object : NativeOpusDecoder {
//            override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int =
//                Opus.INSTANCE.opus_decode(
//                    ptr,
//                    data.copyToArray(),
//                    data.size.toIntSafe(),
//                    pcm.resizedBuffer(),
//                    pcm.size.toIntSafe() / channels,
//                    0
//                )
//
//            override fun destroy() = Opus.INSTANCE.opus_decoder_destroy(ptr)
//        }
//    }
//
//    actual fun encoder_create(
//        Fs: Int,
//        channels: Int,
//        application: Int,
//    ): NativeOpusEncoder {
//        OpusLibrary.loadFromJar()
//
//        val error = IntBuffer.allocate(1)
//        val ptr = Opus.INSTANCE.opus_encoder_create(Fs, channels, application, error)
//        val code = OpusResultCode.valueOf(error[0])
//        if (code != OpusResultCode.Ok) throw OpusException(code.value, "Failed to create encoder: $code")
//
//        return object : NativeOpusEncoder {
//            override fun ctl(request: Int, value: Int): Int =
//                Opus.INSTANCE.opus_encoder_ctl(ptr, request, value)
//
//            override fun encode(pcm: ShortMemorySlice, frameSize: Int, data: ByteMemorySlice): Int =
//                Opus.INSTANCE.opus_encode(
//                    ptr,
//                    pcm.resizedBuffer(),
//                    frameSize,
//                    data.resizedBuffer(),
//                    data.size.toIntSafe()
//                )
//
//            override fun destroy() = Opus.INSTANCE.opus_encoder_destroy(ptr)
//        }
//    }
//
//}