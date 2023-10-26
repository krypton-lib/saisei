package saisei.codec.mp3

import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import java.util.*
import kotlin.jvm.optionals.getOrNull

public interface Mp3DecoderFactory {
    public fun create(codecParameters: CodecParameters): CodecChunkDecoder
}

public interface Mp3EncoderFactory {
    public fun create(codecParameters: CodecParameters): CodecChunkEncoder
}

internal actual fun createMp3Decoder(codecParameters: CodecParameters): CodecChunkDecoder {
    val factory = ServiceLoader.load(Mp3DecoderFactory::class.java)
        .findFirst()
        .getOrNull()
        ?: throw UnsupportedOperationException("No available Mp3 decoder factories on this platform.")

    return factory.create(codecParameters)
}

internal actual fun createMp3Encoder(codecParameters: CodecParameters): CodecChunkEncoder {
    val factory = ServiceLoader.load(Mp3EncoderFactory::class.java)
        .findFirst()
        .getOrNull()
        ?: throw UnsupportedOperationException("No available Mp3 encoder factories on this platform.")

    return factory.create(codecParameters)
}
