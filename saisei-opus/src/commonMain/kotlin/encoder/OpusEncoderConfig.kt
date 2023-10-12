package saisei.codec.opus.encoder

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Configuration used within an [OpusEncoder]
 */
public data class OpusEncoderConfig(
    val application: OpusApplication,
    val options: Set<OpusEncoderOption>,
) {
    public companion object {
        /**
         * The default opus encoding configuration used by Krypton.
         */
        public val Default: OpusEncoderConfig = OpusEncoderConfig {
            application = OpusApplication.Audio

            complexity(10)
            signal(OpusSignal.Music)
            bitrate(OpusBitrate.Automatic)
        }

        /**
         * The highest quality opus encoding configuration.
         */
        public val HighestQuality: OpusEncoderConfig = Default.copy {
            bitrate(OpusBitrate.Max)
        }

        public inline operator fun invoke(block: Builder.() -> Unit): OpusEncoderConfig {
            return Builder()
                .apply(block)
                .build()
        }
    }

    @OptIn(ExperimentalContracts::class)
    public inline fun copy(block: Builder.() -> Unit): OpusEncoderConfig {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return Builder.from(this)
            .apply(block)
            .build()
    }

    public class Builder {
        public companion object {
            public fun from(config: OpusEncoderConfig): Builder {
                val builder = Builder()
                builder.application = config.application
                builder.options += config.options

                return builder
            }
        }

        /**
         *
         */
        public val options: MutableSet<OpusEncoderOption> = mutableSetOf()

        /**
         * The opus application.
         */
        public var application: OpusApplication = OpusApplication.Audio

        public fun complexity(value: Int) {
            options.removeAll { it is OpusComplexity }
            options += OpusComplexity(value)
        }

        public fun signal(value: OpusSignal) {
            options.removeAll { it is OpusSignal }
            options += value
        }

        public fun bitrate(value: Int) {
            options.removeAll { it is OpusBitrate }
            options += when (value) {
                -1 -> OpusBitrate.Max
                -1000 -> OpusBitrate.Automatic
                else -> OpusBitrate.Custom(value)
            }
        }

        public fun bitrate(value: OpusBitrate) {
            options.removeAll { it is OpusBitrate }
            options += value
        }

        public operator fun OpusEncoderOption.unaryPlus() {
            options += this
        }

        /**
         *
         */
        public fun build(): OpusEncoderConfig = OpusEncoderConfig(application, options)
    }
}
