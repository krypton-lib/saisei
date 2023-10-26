package saisei.codec.opus.encoder

public enum class OpusApplication(public val value: Int) {
    /**
     * Best for most VoIP/videoconference applications where listening quality and intelligibility matter most.
     */
    VOIP(2048),

    /**
     * Best for broadcast/high-fidelity application where the decoded audio should be as close as possible to the input.
     */
    Audio(2049)
}
