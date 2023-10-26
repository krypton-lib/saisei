package saisei.codec.pcm

import naibu.platform.Platform
import saisei.codec.Codec
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.io.platform.isLittleEndian

public data class PCM internal constructor(
    val bigEndian: Boolean = false,
    val bitDepth: Int,
) : Codec("PCM_${if (bigEndian) "BE" else "LE"}", true) {
    public companion object {
        public val S16_BE: PCM = PCM(true, 16)

        public val S16_LE: PCM = PCM(false, 16)

        public val S16_Native: PCM get() = if (Platform.isLittleEndian) S16_LE else S16_BE
    }

    override fun createDecoder(params: CodecParameters): CodecChunkDecoder {
        TODO("Not yet implemented")
    }

    override fun createEncoder(params: CodecParameters): CodecChunkEncoder {
        TODO("Not yet implemented")
    }
}