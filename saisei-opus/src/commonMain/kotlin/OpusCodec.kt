package saisei.codec.opus

import saisei.codec.Codec
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.codec.opus.decoder.OpusChunkDecoder
import saisei.codec.opus.encoder.OpusChunkEncoder
import saisei.codec.opus.encoder.OpusEncoderConfig
import saisei.io.slice.ByteSlice

public data class OpusCodec(
    val encoderConfig: OpusEncoderConfig = OpusEncoderConfig.Default,
) : Codec("OPUS", true) {

    override fun getSampleCount(params: CodecParameters, packet: ByteSlice): Int =
        OpusPacketTools.getPacketSampleCount(params.sampleRate, packet)

    override fun createDecoder(params: CodecParameters): CodecChunkDecoder = OpusChunkDecoder(params)

    override fun createEncoder(params: CodecParameters): CodecChunkEncoder = OpusChunkEncoder(params, encoderConfig)
}
