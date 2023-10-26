package saisei.codec.mp3

import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters

internal expect fun createMp3Decoder(codecParameters: CodecParameters): CodecChunkDecoder

internal expect fun createMp3Encoder(codecParameters: CodecParameters): CodecChunkEncoder
