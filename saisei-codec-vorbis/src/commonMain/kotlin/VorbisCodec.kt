package saisei.codec.vorbis

import saisei.codec.Codec
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters

public object VorbisCodec : Codec("vorbis", true) {
    override fun createDecoder(params: CodecParameters): CodecChunkDecoder {
        TODO("Not yet implemented")
    }

    override fun createEncoder(params: CodecParameters): CodecChunkEncoder {
        TODO("Not yet implemented")
    }
}