package saisei.codec.opus.encoder

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The encoder's bitrate, supports 500 to 512000 bits-per-second.
 */
@Serializable(with = OpusBitrate.Companion::class)
public sealed class OpusBitrate(value: Int) : OpusEncoderOption(BITRATE, value) {
    public companion object : KSerializer<OpusBitrate> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("OpusBitrate", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: OpusBitrate) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): OpusBitrate {
            val br = decoder.decodeInt()
            return valueOf(br) ?: throw SerializationException("Invalid bitrate value: $br")
        }

        public fun valueOf(value: Int): OpusBitrate? = when (value) {
            -1 -> Max
            -1000 -> Automatic
            in 5..512 -> Custom(value * 100)
            in 500..512000 -> Custom(value)
            else -> null
        }
    }

    /**
     * Use the max bitrate possible (512kbps)
     */
    public object Max : OpusBitrate(-1) {
        override fun toString(): String = "OpusBitrate::Max"
    }

    /**
     * Automatically detects the bitrate
     */
    public object Automatic : OpusBitrate(-1000) {
        override fun toString(): String = "OpusBitrate::Automatic"
    }

    /**
     * A custom bitrate between 5kbps to 512kbps
     */
    public class Custom(value: Int) : OpusBitrate(value) {
        init {
            require(value in 500..512000) { "Bitrate must range between 500 and 512000" }
        }

        override fun toString(): String {
            return "OpusBitrate::Custom(value=$value)"
        }
    }
}

/**
 * The encoder's computational complexity.
 *
 * @param value number in the range 0-10, with 10 representing the highest complexity.
 */
@Serializable(with = OpusComplexity.Serializer::class)
public class OpusComplexity(value: Int) : OpusEncoderOption(COMPLEXITY, value) {
    public object Serializer : KSerializer<OpusComplexity> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("OpusComplexity", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: OpusComplexity) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): OpusComplexity {
            val value = decoder.decodeInt()
            if (value !in 0..10) {
                throw SerializationException("Value $value is out of range: 0..10")
            }

            return OpusComplexity(value)
        }
    }

    init {
        require(value in 0..10) { "Complexity must range between 0 and 10" }
    }

    override fun toString(): String = "OpusComplexity(value=$value)"
}

/**
 * The type of signal being encoded.
 */
@Serializable(with = OpusSignal.Serializer::class)
public sealed class OpusSignal(value: Int) : OpusEncoderOption(SIGNAL, value) {
    public companion object {
        public fun values(): Array<OpusSignal> = arrayOf(Voice, Music)

        public fun valueOf(name: String): OpusSignal? = when (name.lowercase()) {
            "voice" -> Voice
            "music" -> Music
            else -> null
        }
    }

    public object Serializer : KSerializer<OpusSignal> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("OpusSignal[String]", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: OpusSignal) {
            val name = when (value) {
                is Voice -> "voice"
                is Music -> "music"
            }

            encoder.encodeString(name)
        }

        override fun deserialize(decoder: Decoder): OpusSignal {
            val name = decoder.decodeString()
            return valueOf(name) ?: error("Invalid bitrate signal: $name")
        }
    }

    /**
     * Best for most VoIP/videoconference applications where listening quality and intelligibility matter most.
     */
    public object Voice : OpusSignal(3001) {
        override fun toString(): String = "OpusSignal::Voice"
    }

    /**
     * Only use when lowest-achievable latency is what matters most. Voice-optimized modes cannot be used.
     */
    public object Music : OpusSignal(3002) {
        override fun toString(): String = "OpusSignal::Music"
    }
}
