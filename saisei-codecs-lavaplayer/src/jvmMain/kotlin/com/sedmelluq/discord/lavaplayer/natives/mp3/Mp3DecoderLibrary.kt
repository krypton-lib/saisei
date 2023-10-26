package com.sedmelluq.discord.lavaplayer.natives.mp3

import saisei.codecs.loadNatives
import java.nio.ByteBuffer
import java.nio.ShortBuffer

internal class Mp3DecoderLibrary {
    external fun create(): Long

    external fun destroy(instance: Long)

    external fun decode(
        instance: Long,
        directInput: ByteBuffer,
        inputLength: Int,
        directOutput: ShortBuffer,
        outputLengthInBytes: Int,
    ): Int

    companion object {
        val INSTANCE: Mp3DecoderLibrary
            get() {
                loadNatives()
                return Mp3DecoderLibrary()
            }
    }
}
