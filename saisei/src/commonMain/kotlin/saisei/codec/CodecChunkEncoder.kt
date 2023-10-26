package saisei.codec

import naibu.common.Closeable
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public interface CodecChunkEncoder : Closeable {
    override fun close() {}

    /**
     * @param pcm  The PCM encoded input.
     * @param data An output for the encoded PCM.
     */
    public fun encode(pcm: ShortMemorySlice, data: ByteMemorySlice): ByteMemorySlice
}