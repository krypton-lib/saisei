package saisei.container.wav

import kotlinx.serialization.Serializable
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.stream.SeekableReadStream

@Serializable
public data class WavDataChunk(override val header: WavChunkHeader, val start: Long) : WavChunk() {
    public companion object : WavChunkMarshallFactory<WavDataChunk>() {
        override val id: String = "data"

        override suspend fun unmarshall(header: WavChunkHeader, stream: SeekableReadStream): WavDataChunk =
            WavDataChunk(header, stream.position)

        override suspend fun marshall(value: WavDataChunk, cursor: ByteWriteCursor) {
            TODO("Not Implemented")
        }
    }
}
