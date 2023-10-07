package saisei.codec.opus.encoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public expect class OpusEncoder(parameters: CodecParameters, config: OpusEncoderConfig) : Closeable {
    /**
     * Encodes the provided [pcm] and writes it to [opus]
     *
     * @param input  The 16-bit pcm array
     * @param output Output UByteArray for encoded opus
     * @return The resized [output].
     */
    public fun encode(
        input: ShortMemorySlice,
        output: ByteMemorySlice,
    ): ByteMemorySlice
}