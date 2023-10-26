package saisei.codec

import saisei.io.slice.ByteSlice

public abstract class Codec(public val name: String, public val isAudio: Boolean) {

    /**
     * The ID of this codec.
     */
    public val id: String get() = "${if (isAudio) "A" else "V"}_${name.uppercase()}"

    /**
     *
     */
    public open fun getSampleCount(params: CodecParameters, packet: ByteSlice): Int = params.frameSampleCount

    /**
     *
     */
    public abstract fun createDecoder(params: CodecParameters): CodecChunkDecoder

    /**
     *
     */
    public abstract fun createEncoder(params: CodecParameters): CodecChunkEncoder
}
