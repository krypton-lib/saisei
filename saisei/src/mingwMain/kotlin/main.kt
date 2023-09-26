package gay.vzt.saisei

import kotlinx.cinterop.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import naibu.io.asBytes
import naibu.io.memory.DefaultAllocator
import naibu.io.order.ByteOrder
import naibu.io.slice.get
import naibu.io.slice.writeShort
import platform.windows.*
import kotlin.math.PI
import kotlin.math.sin

const val SAMPLE_RATE = 44_100
const val CHANNELS = 2
const val BIT_DEPTH = 16

const val BUFFER_SIZE = SAMPLE_RATE * CHANNELS * (BIT_DEPTH / 8)

@OptIn(ExperimentalForeignApi::class)
fun main(): Unit = runBlocking {
    LoadLibraryW("winmm.dll")

    memScoped {
        /* initialize WAVE format. */
        val waveFormat = cValue<WAVEFORMATEX> {
            wFormatTag = WAVE_FORMAT_PCM.convert()
            nChannels = CHANNELS.convert()
            nSamplesPerSec = SAMPLE_RATE.convert()
            wBitsPerSample = BIT_DEPTH.convert()
            nBlockAlign = (CHANNELS * BIT_DEPTH.asBytes).convert()
            nAvgBytesPerSec = BUFFER_SIZE.convert()
        }

        // allocate playback buffer.
        val playbackBuffer = DefaultAllocator.allocate(BUFFER_SIZE)

        // Initialize and open the audio device
        val hWaveOut = alloc<HWAVEOUTVar>()
        var result = waveOutOpen(
            hWaveOut.ptr,
            WAVE_MAPPER,
            waveFormat.ptr,
            CALLBACK_NULL.convert(),
            0u,
            CALLBACK_NULL.convert()
        )

        if (result != MMSYSERR_NOERROR.toUInt()) {
            println("Failed to open audio device")
            return@memScoped
        }

        // Generate audio data (simple sine wave)
        for (i in playbackBuffer.indices step 4) {
            val sampleValue = (sin(2 * PI * 440.0 * i / SAMPLE_RATE) * 32767).toInt().toShort()
            playbackBuffer[0..<2].writeShort(sampleValue, ByteOrder.Native)
            playbackBuffer[2..<4].writeShort(sampleValue, ByteOrder.Native)
        }

        // Prepare and play the audio
        val waveHdr = cValue<WAVEHDR> {
            lpData = playbackBuffer.pointer
            dwBufferLength = BUFFER_SIZE.convert()
            dwFlags = 0u
        }

        result = waveOutPrepareHeader(hWaveOut.value, waveHdr.ptr, waveHdr.size.convert())
        if (result == MMSYSERR_NOERROR.toUInt()) {
            waveOutWrite(hWaveOut.value, waveHdr.ptr, waveHdr.size.convert())
        }

        // Wait for audio playback to finish
        waveHdr.useContents {
            while ((dwFlags and WHDR_DONE.toUInt()) == 0u) {
                delay(10)
            }
        }

        // Clean up
        waveOutUnprepareHeader(hWaveOut.value, waveHdr.ptr, waveHdr.size.convert())
        waveOutClose(hWaveOut.value)
        playbackBuffer.close()
    }
}
