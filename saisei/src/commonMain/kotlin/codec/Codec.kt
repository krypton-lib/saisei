package saisei.codec

import naibu.platform.Platform
import saisei.io.platform.isLittleEndian
import saisei.io.slice.ByteSlice

public abstract class Codec(public val name: String, public val isAudio: Boolean) {

    /**
     * The ID of this codec.
     */
    public val id: String get() = "${if (isAudio) "A" else "V"}_$name"

    public open fun getSampleCount(params: CodecParameters, packet: ByteSlice): Int = params.chunkSampleCount

    public abstract fun createDecoder(params: CodecParameters): CodecChunkDecoder

    public abstract fun createEncoder(params: CodecParameters): CodecChunkEncoder

    public data class PCM internal constructor(
        val bigEndian: Boolean = false,
        val bitDepth: Int,
    ) : Codec("PCM_${if (bigEndian) "BE" else "LE"}", true) {
        public companion object {
            public val S16_BE: PCM = PCM(true, 16)
            public val S16_LE: PCM = PCM(false, 16)

            public fun shortNative(): PCM = if (Platform.isLittleEndian) S16_LE else S16_BE
        }

        override fun createDecoder(params: CodecParameters): CodecChunkDecoder {
            TODO("Not yet implemented")
        }

        override fun createEncoder(params: CodecParameters): CodecChunkEncoder {
            TODO("Not yet implemented")
        }
    }
}