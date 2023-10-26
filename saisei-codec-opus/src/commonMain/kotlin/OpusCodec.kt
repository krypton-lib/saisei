package saisei.codec.opus

import saisei.codec.Codec
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.codec.opus.decoder.OpusDecoder
import saisei.codec.opus.encoder.OpusEncoder
import saisei.codec.opus.encoder.OpusEncoderConfig
import saisei.io.slice.ByteSlice

public data class OpusCodec(
    val encoderConfig: OpusEncoderConfig = OpusEncoderConfig.Default,
) : Codec("opus", true) {
    override fun getSampleCount(params: CodecParameters, packet: ByteSlice): Int =
        OpusPacketTools.getPacketSampleCount(params.sampleRate, packet)

    override fun createDecoder(params: CodecParameters): CodecChunkDecoder = OpusDecoder(params)

    override fun createEncoder(params: CodecParameters): CodecChunkEncoder = OpusEncoder(params, encoderConfig)
}
