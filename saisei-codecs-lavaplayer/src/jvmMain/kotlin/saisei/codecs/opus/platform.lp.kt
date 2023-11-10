package saisei.codecs.opus

import com.sedmelluq.discord.lavaplayer.natives.opus.OpusDecoderLibrary
import com.sedmelluq.discord.lavaplayer.natives.opus.OpusEncoderLibrary
import com.sedmelluq.lava.common.natives.NativeResourceHolder
import saisei.codec.opus.NativeOpusDecoder
import saisei.codec.opus.NativeOpusEncoder
import saisei.codec.opus.NativeOpusLibrary
import saisei.codec.opus.encoder.OpusEncoderOption
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.resizedBuffer
import saisei.io.slice.size32

internal class LavaplayerNativeOpusLibrary : NativeOpusLibrary {
    override fun createDecoder(sampleRate: Int, channels: Int): NativeOpusDecoder =
        Dec(sampleRate, channels)

    override fun createEncoder(sampleRate: Int, channels: Int, application: Int): NativeOpusEncoder =
        Enc(sampleRate, channels, application)

    class Enc(
        private val sampleRate: Int,
        private val channels: Int,
        private val application: Int,
    ) : NativeOpusEncoder, NativeResourceHolder() {
        private var ins: Long? = null
        private val lib = OpusEncoderLibrary.INSTANCE
        private val ctl = mutableMapOf<Int, Int>()

        override val supportsCTL: Boolean
            get() = true

        override fun ctl(request: Int, value: Int): Int {
            ctl[request] = value
            return 0
        }

        override fun freeResources() {
            ins?.let(lib::destroy)
        }

        override fun encode(pcm: ShortMemorySlice, frameSize: Int, data: ByteMemorySlice): Int {
            // TODO: shitty little workaround to correctly set the encode complexity
            if (ins == null) ins = lib.create(
                sampleRate,
                channels,
                application,
                ctl[OpusEncoderOption.COMPLEXITY] ?: 10
            )

            checkNotReleased()
            return lib.encode(ins!!, pcm.resizedBuffer(), frameSize, data.resizedBuffer(), data.size32)
        }
    }

    class Dec(
        sampleRate: Int,
        private val channels: Int,
    ) : NativeOpusDecoder, NativeResourceHolder() {
        private val lib = OpusDecoderLibrary.INSTANCE
        private val ins = lib.create(sampleRate, channels)

        override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): Int {
            checkNotReleased()
            return lib.decode(ins, data.resizedBuffer(), data.size32, pcm.resizedBuffer(), pcm.size32 / channels)
        }

        override fun freeResources() {
            lib.destroy(ins)
        }
    }
}
