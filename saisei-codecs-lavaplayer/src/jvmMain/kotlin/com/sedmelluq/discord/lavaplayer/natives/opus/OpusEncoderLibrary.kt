package com.sedmelluq.discord.lavaplayer.natives.opus

import saisei.codecs.loadNatives
import java.nio.ByteBuffer
import java.nio.ShortBuffer

internal class OpusEncoderLibrary private constructor() {
    external fun create(sampleRate: Int, channels: Int, application: Int, quality: Int): Long

    external fun destroy(ins: Long)

    external fun encode(ins: Long, src: ShortBuffer, frameSize: Int, dst: ByteBuffer, dstLen: Int): Int

    companion object {
        val INSTANCE: OpusEncoderLibrary
            get() {
                loadNatives()
                return OpusEncoderLibrary()
            }
    }
}
