package saisei.codec.opus.encoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public actual class OpusEncoder actual constructor(
    public val parameters: CodecParameters,
    public val config: OpusEncoderConfig,
) : Closeable {
    public actual fun encode(input: ShortMemorySlice, output: ByteMemorySlice): ByteMemorySlice {
        TODO()
    }

    override fun close() {
    }
}