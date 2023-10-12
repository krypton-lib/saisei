package saisei.codec.opus

import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.ByteArraySlice
import saisei.io.slice.impl.ByteMemorySlice
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public sealed class OpusPacket {
    /**
     * This packet's sample rate.
     */
    public abstract val sampleRate: Int

    /**
     * The number of samples in this packet.
     */
    public val sampleCount: Int by lazy {
        OpusPacketTools.getPacketSampleCount(sampleRate, asSlice())
    }

    /**
     * The number of frames in this packet.
     */
    public val frameCount: Int by lazy {
        OpusPacketTools.getPacketFrameCount(asSlice())
    }

    /**
     * The duration of this packet.
     */
    public val duration: Duration
        get() = sampleCount.seconds / sampleRate

    protected abstract fun asSlice(): ByteSlice

    public class Native(
        public val data: ByteMemorySlice,
        override val sampleRate: Int,
    ) : OpusPacket() {
        override fun asSlice(): ByteSlice = data
    }

    public class Array(
        public val data: ByteArraySlice,
        override val sampleRate: Int,
    ) : OpusPacket() {
        override fun asSlice(): ByteSlice = data
    }
}