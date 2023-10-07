package saisei.codec.opus.decoder

import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusChunkDecoder(parameters: CodecParameters) : CodecChunkDecoder {
    private val dec = OpusDecoder(parameters)

    override fun decode(encoded: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice = dec.decode(encoded, pcm)
    override fun close(): Unit = dec.close()
}