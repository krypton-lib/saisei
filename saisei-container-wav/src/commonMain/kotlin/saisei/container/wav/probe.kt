package saisei.container.wav

import naibu.logging.logging
import saisei.io.exception.EOFException
import saisei.io.format.riff.RiffHeader
import saisei.io.format.riff.readRiffHeader
import saisei.io.stream.SeekableReadStream
import kotlin.properties.Delegates

public const val WAVE_FORMAT_PCM: Short = 0x0001

public data class WavFile(
    val fmt: WavFormatChunk,
    val sampleAreaSize: Long,
    val startOffset: Long,
) {
    val blockCount: Long
        get() = sampleAreaSize / fmt.wBlockAlign.toInt()

    val padding: Int
        get() = fmt.wBlockAlign.toInt() - fmt.wChannels.toInt() * (fmt.wBitsPerSample.toInt() shr 3)

    public class Builder {
        public lateinit var fmt: WavFormatChunk
        public var sampleAreaSize: Long by Delegates.notNull()
        public var startOffset: Long by Delegates.notNull()

        public fun build(): WavFile {
            check(fmt.wFormatTag == WAVE_FORMAT_PCM) {
                "Invalid audio format ${fmt.wFormatTag}, must be PCM"
            }

            check(fmt.wChannels.toInt() in 1..16) {
                "Invalid channel count: ${fmt.wChannels}"
            }

            val minimumBlockAlign = fmt.wChannels.toInt() * (fmt.wBitsPerSample.toInt() shr 3)

            check(!(fmt.wBlockAlign.toInt() < minimumBlockAlign || fmt.wBlockAlign.toInt() > minimumBlockAlign + 32)) {
                "Block align is not valid: ${fmt.wBlockAlign}"
            }

            check(fmt.wBlockAlign.toInt() % (fmt.wBitsPerSample.toInt() shr 3) == 0) {
                "Block align is not a multiple of bits per sample: ${fmt.wBlockAlign}"
            }

            return WavFile(fmt, sampleAreaSize, startOffset)
        }
    }
}

internal val log by logging("krypton.format.wav.WavProbe")

public class WavChunkReader(private val stream: SeekableReadStream, private val header: RiffHeader) {
    private var consumed = 0L

    public suspend fun finish() {
        if (consumed < header.size.toLong()) {
            val remaining = this.header.size.toLong() - consumed
            stream.discardFully(remaining)
            consumed += remaining
        }

        /* pad the chunk to the next 2-byte boundary. */
        if (header.size and 0x1u == 1u) {
            stream.read()
        }
    }

    public suspend fun next(): WavChunk? {
        while (true) {
            /* align to the next 2-byte boundary. */
            if (consumed and 0x1 == 1.toLong()) {
                stream.read()
                consumed++
            }

            /* check if there are enough bytes for another chunk. */
            if (consumed + 8 > header.size.toLong()) {
                return null
            }

            /* read chunk header. */
            val header = stream.readWavChunkHeader()
            consumed += 8

            val result = stream.readWavChunk(header)
            if (result == null) {
                /* as per RIFF spec, ignore unknown chunks. */
                stream.discardFully(header.dataSize.toLong())
                log.info { "Ignoring unknown WAV chunk: $header" }
                continue
            }

            /* consume the size of the chunk. */
            consumed += header.dataSize

            // return it lol.
            return result
        }
    }
}

public suspend fun probeWavFile(stream: SeekableReadStream): WavFile {
    val riff = readRiffHeader(stream)
    require(riff.form == "WAVE") {
        "RIFF form is not 'WAVE', instead it's '${riff.form}'"
    }

    val builder = WavFile.Builder()
    val reader = WavChunkReader(stream, riff)
    while (true) {
        when (val chunk = reader.next() ?: throw EOFException()) {
            is WavFormatChunk -> {
                builder.fmt = chunk
            }

            is WavListChunk -> {
                stream.discardFully(chunk.length.toLong())
                continue
            }

            is WavDataChunk -> {
                builder.sampleAreaSize = chunk.header.dataSize.toLong()
                builder.startOffset = chunk.start
                return builder.build()
            }

            else -> {}
        }
    }
}
