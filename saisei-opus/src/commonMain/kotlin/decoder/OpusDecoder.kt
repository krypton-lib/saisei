package saisei.codec.opus.decoder

import naibu.common.Closeable
import saisei.codec.CodecParameters
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice

public expect class OpusDecoder(parameters: CodecParameters) : Closeable {
    /**
     * Encodes the provided [output] and writes it to [input]
     *
     * @param input  Encoded opus buffer to decode.
     * @param output The 16-bit pcm output buffer
     * @return The resized [output] buffer that contains the decoded samples.
     */
    public fun decode(input: ByteMemorySlice, output: ShortMemorySlice): ShortMemorySlice
}