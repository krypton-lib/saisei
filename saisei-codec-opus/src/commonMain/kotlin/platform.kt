package saisei.codec.opus

import naibu.common.Closeable
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

internal expect fun getOpusLibrary(): NativeOpusLibrary

public interface NativeOpusLibrary {
    public fun createDecoder(sampleRate: Int, channels: Int): NativeOpusDecoder

    public fun createEncoder(sampleRate: Int, channels: Int, application: Int): NativeOpusEncoder
}

public interface NativeOpusDecoder : Closeable {
    public fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int
}

public interface NativeOpusEncoder : Closeable {
    public val supportsCTL: Boolean

    public fun ctl(request: Int, value: Int): Int

    public fun encode(pcm: ShortMemorySlice, frameSize: Int, data: ByteMemorySlice): Int
}
