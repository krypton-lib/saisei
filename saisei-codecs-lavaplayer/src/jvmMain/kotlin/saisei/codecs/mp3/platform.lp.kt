package saisei.codecs.mp3

import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecParameters
import saisei.codec.mp3.Mp3DecoderFactory

internal class LavaplayerMp3DecoderFactory : Mp3DecoderFactory {
    override fun create(codecParameters: CodecParameters): CodecChunkDecoder = Mp3Decoder()
}
