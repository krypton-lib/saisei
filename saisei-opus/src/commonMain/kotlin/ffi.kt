package saisei.codec.opus

import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

internal expect object opus {
    fun decoder_create(Fs: Int, channels: Int): opus_decoder

    fun encoder_create(Fs: Int, channels: Int, application: Int): opus_encoder
}

internal interface opus_decoder {
    fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int
    fun destroy()
}

internal interface opus_encoder {
    fun ctl(request: Int, value: Int): Int
    fun encode(pcm: ShortMemorySlice, frame_size: Int, data: ByteMemorySlice): Int
    fun destroy()
}
