package saisei.container.wav

import saisei.io.format.riff.readID
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.stream.SeekableReadStream

public data class WavListChunk(
    override val header: WavChunkHeader,
    val form: String,
    val length: Int,
) : WavChunk() {
    public companion object : WavChunkMarshallFactory<WavListChunk>() {
        override val id: String = "LIST"

        override suspend fun unmarshall(header: WavChunkHeader, stream: SeekableReadStream): WavListChunk {
            val form = stream.readID()
            return WavListChunk(header, form, header.dataSize - 4)
        }

        override suspend fun marshall(value: WavListChunk, cursor: ByteWriteCursor) {
            TODO("Not yet implemented")
        }
    }
}
