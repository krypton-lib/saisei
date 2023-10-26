package saisei.codec.opus.encoder

/**
 * Represents an Opus option.
 */
public open class OpusEncoderOption(public val request: Int, public val value: Int) {
    public companion object {
        public const val BITRATE: Int = 4002

        public const val COMPLEXITY: Int = 4010

        public const val SIGNAL: Int = 4024
    }

    override fun toString(): String = "OpusOption(request=$request, value=$value)"
}
