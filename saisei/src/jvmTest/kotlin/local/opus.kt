package local

import com.sun.jna.ptr.PointerByReference
import log
import naibu.io.memory.Memory
import naibu.io.memory.get
import saisei.container.mkv.MatroskaTrack
import tomp2p.opuswrapper.Opus
import java.nio.IntBuffer
import kotlin.system.exitProcess

val ERR_BUFFER = IntBuffer.allocate(1)

fun readError() {
    log.error { ERR_BUFFER.get(0).readOpusError() }
}

fun checkErr() {
    if (ERR_BUFFER.get(0) != Opus.OPUS_OK) {
        readError()
        exitProcess(1)
    }
}

fun Int.readOpusError(): String {
    val b = StringBuilder("Decoder failed to decode audio, code: ")
    when (this) {
        Opus.OPUS_BAD_ARG -> b.append("OPUS_BAD_ARG")
        Opus.OPUS_BUFFER_TOO_SMALL -> b.append("OPUS_BUFFER_TOO_SMALL")
        Opus.OPUS_INTERNAL_ERROR -> b.append("OPUS_INTERNAL_ERROR")
        Opus.OPUS_INVALID_PACKET -> b.append("OPUS_INVALID_PACKET")
        Opus.OPUS_UNIMPLEMENTED -> b.append("OPUS_UNIMPLEMENTED")
        Opus.OPUS_INVALID_STATE -> b.append("OPUS_INVALID_STATE")
        Opus.OPUS_ALLOC_FAIL -> b.append("OPUS_ALLOC_FAIL")
        else -> b.append(this)
    }

    return b.toString()
}

fun MatroskaTrack.setupOpusDecoder(): PointerByReference {
    // require Opus
    require(codec.id == "A_OPUS") { "Codec is not A_OPUS: ${codec.id}" }

    // create a new opus decoder
    val opusDec = Opus.INSTANCE.opus_decoder_create(
        audio!!.samplingFrequency.toInt(),
        audio!!.channelCount,
        ERR_BUFFER
    )

    checkErr()

    Opus.INSTANCE.opus_decoder_ctl(
        opusDec,
        Opus.OPUS_SET_APPLICATION_REQUEST,
        Opus.OPUS_APPLICATION_AUDIO
    )

    checkErr()
    return opusDec
}


object OpusDec {
    fun getPacketFrameSize(
        sampleRate: Int,
        buffer: Memory,
        offset: Long = 0,
        length: Long = buffer.size,
    ): Int {
        if (length < 1) {
            return 0
        }

        val frameCount = getPacketFrameCount(buffer, offset, length)
        if (frameCount < 0) {
            return 0
        }

        val samples = frameCount * getPacketSamplesPerFrame(sampleRate, buffer[offset].toInt())
        return if (samples * 25 > sampleRate * 3) {
            0
        } else {
            samples
        }
    }

    private fun getPacketFrameCount(buffer: Memory, offset: Long, length: Long): Int {
        return when {
            buffer[offset].toInt() and 0x03 == 0 ->
                1

            buffer[offset].toInt() and 0x03 == 3 ->
                if (length < 2) -1 else buffer[offset + 1].toInt() and 0x3F

            else -> 2
        }
    }

    private fun getPacketSamplesPerFrame(frequency: Int, firstByte: Int): Int {
        val shiftBits = (firstByte shr 3) and 0x03
        return when {
            firstByte and 0x80 != 0 ->
                (frequency shl shiftBits) / 400

            firstByte and 0x60 == 0x60 ->
                if (firstByte and 0x08 != 0) frequency / 50 else frequency / 100

            shiftBits == 3 ->
                frequency * 60 / 1000

            else -> (frequency shl shiftBits) / 100
        }
    }
}
