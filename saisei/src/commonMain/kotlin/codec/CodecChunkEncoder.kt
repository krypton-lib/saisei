package saisei.codec

import naibu.common.Closeable
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public interface CodecChunkEncoder : Closeable {
    override fun close() {}

    /**
     * @param dec The PCM encoded input.
     * @param enc An output for the encoded PCM.
     */
    public fun encode(dec: ShortMemorySlice, enc: ByteMemorySlice): ByteMemorySlice
}