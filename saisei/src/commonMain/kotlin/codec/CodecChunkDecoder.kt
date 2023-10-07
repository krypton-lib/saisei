package saisei.codec

import naibu.common.Closeable
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public interface CodecChunkDecoder : Closeable {
    override fun close() {}

    public fun decode(encoded: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice
}