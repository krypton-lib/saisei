package saisei.container.wav

import kotlinx.serialization.Serializable
import saisei.io.format.riff.*
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.stream.SeekableReadStream

@Serializable
public data class WavFormatChunk(
    override val header: WavChunkHeader,
    val wFormatTag: Short,
    val wChannels: UShort,
    val dwSamplesPerSec: UInt,
    val dwAvgBytesPerSec: UInt,
    val wBlockAlign: UShort,
    val wBitsPerSample: UShort,
) : WavChunk() {
    public companion object : WavChunkMarshallFactory<WavFormatChunk>() {
        override val id: String get() = "fmt "

        override suspend fun marshall(value: WavFormatChunk, cursor: ByteWriteCursor) {
            cursor.writeShortLE(value.wFormatTag)
            cursor.writeUShortLE(value.wChannels)
            cursor.writeUIntLE(value.dwSamplesPerSec)
            cursor.writeUIntLE(value.dwAvgBytesPerSec)
            cursor.writeUShortLE(value.wBlockAlign)
            cursor.writeUShortLE(value.wBitsPerSample)
        }

        override suspend fun unmarshall(header: WavChunkHeader, stream: SeekableReadStream): WavFormatChunk =
            WavFormatChunk(
                header,
                stream.readShortLE(),
                stream.readUShortLE(),
                stream.readUIntLE(),
                stream.readUIntLE(),
                stream.readUShortLE(),
                stream.readUShortLE(),
            )
    }
}
