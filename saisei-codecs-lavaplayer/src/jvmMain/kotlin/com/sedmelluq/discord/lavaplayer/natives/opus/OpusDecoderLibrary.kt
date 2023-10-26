package com.sedmelluq.discord.lavaplayer.natives.opus

import saisei.codecs.loadNatives
import java.nio.ByteBuffer
import java.nio.ShortBuffer

internal class OpusDecoderLibrary private constructor() {
    external fun create(sampleRate: Int, channels: Int): Long

    external fun destroy(ins: Long)

    external fun decode(ins: Long, src: ByteBuffer, srcLen: Int, dst: ShortBuffer, frameSize: Int): Int

    companion object {
        val INSTANCE: OpusDecoderLibrary
            get() {
                loadNatives()
                return OpusDecoderLibrary()
            }
    }
}
