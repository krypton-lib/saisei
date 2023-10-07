package saisei.codec.opus.decoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public actual class OpusDecoder actual constructor(public val parameters: CodecParameters) : Closeable {
    public actual fun decode(input: ByteMemorySlice, output: ShortMemorySlice): ShortMemorySlice {
        TODO()
    }

    override fun close() {
    }
}