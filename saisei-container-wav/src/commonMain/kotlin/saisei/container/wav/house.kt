package saisei.container.wav

import saisei.io.format.riff.readID
import saisei.io.format.riff.readIntLE
import saisei.io.stream.SeekableReadStream

public val FACTORIES: Set<WavChunkMarshallFactory<out WavChunk>> = setOf(
    WavCueChunk,
    WavDataChunk,
    WavListChunk,
    WavFormatChunk
)

public suspend fun SeekableReadStream.readWavChunkHeader(): WavChunkHeader {
    val start = position
    return WavChunkHeader(start, readID(), readIntLE(), position - start)
}

public suspend fun SeekableReadStream.readWavChunk(): WavChunk? =
    readWavChunk(readWavChunkHeader())

public suspend fun SeekableReadStream.readWavChunk(header: WavChunkHeader): WavChunk? =
    FACTORIES.find { it.id == header.id }?.unmarshall(header, this)
