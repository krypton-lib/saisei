package saisei.codec.opus.encoder

import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public class OpusChunkEncoder(parameters: CodecParameters, config: OpusEncoderConfig) : CodecChunkEncoder {
    private val encoder = OpusEncoder(parameters, config)

    override fun encode(dec: ShortMemorySlice, enc: ByteMemorySlice): ByteMemorySlice = encoder.encode(dec, enc)
}