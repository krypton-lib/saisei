package saisei.codec

import naibu.common.Closeable
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public interface CodecChunkDecoder : Closeable {
    public fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice

    override fun close() {}
}