package saisei.codec.mp3

import saisei.codec.Codec
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters

public object Mp3Codec : Codec("Mp3", true) {
    override fun createDecoder(params: CodecParameters): CodecChunkDecoder = createMp3Decoder(params)

    override fun createEncoder(params: CodecParameters): CodecChunkEncoder = createMp3Encoder(params)
}

