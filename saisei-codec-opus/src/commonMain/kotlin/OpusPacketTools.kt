package saisei.codec.opus

import saisei.io.slice.ByteSlice

public object OpusPacketTools {
    /**
     * Get the number of samples in an Opus packet.
     *
     * @param sampleRate The sample rate of the Opus packet
     * @param packet     The opus packet data.
     */
    public fun getPacketSampleCount(
        sampleRate: Int,
        packet: ByteSlice,
    ): Int {
        if (packet.size < 1) {
            return 0
        }

        val frameCount = getPacketFrameCount(packet)
        if (frameCount < 0) {
            return 0
        }

        val samples = frameCount * getPacketSamplesPerFrame(sampleRate, packet[0].toInt())
        return if (samples * 25 > sampleRate * 3) {
            0
        } else {
            samples
        }
    }

    /**
     * Get the number of frames in an Opus packet.
     */
    public fun getPacketFrameCount(slice: ByteSlice): Int = when {
        slice[0].toInt() and 0x03 == 0 ->
            1

        slice[0].toInt() and 0x03 == 3 ->
            if (slice.size < 2) -1 else slice[1].toInt() and 0x3F

        else -> 2
    }

    private fun getPacketSamplesPerFrame(sampleRate: Int, firstByte: Int): Int {
        val shiftBits = (firstByte shr 3) and 0x03
        return when {
            firstByte and 0x80 != 0 ->
                (sampleRate shl shiftBits) / 400

            firstByte and 0x60 == 0x60 ->
                if (firstByte and 0x08 != 0) sampleRate / 50 else sampleRate / 100

            shiftBits == 3 ->
                sampleRate * 60 / 1000

            else -> (sampleRate shl shiftBits) / 100
        }
    }
}