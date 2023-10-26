package saisei.container.wav

import saisei.annotation.ExperimentalSaiseiApi
import saisei.io.charset.Charsets
import saisei.io.format.riff.writeIntLE
import saisei.io.slice.ByteSlice
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.slice.cursor.write
import saisei.io.slice.cursor.writer
import saisei.io.slice.impl.asSlice
import saisei.io.slice.size32
import saisei.io.stream.SeekableReadStream

public abstract class WavChunkMarshallFactory<T : WavChunk> {
    public abstract val id: String

    public abstract suspend fun unmarshall(header: WavChunkHeader, stream: SeekableReadStream): T

    public abstract suspend fun marshall(value: T, cursor: ByteWriteCursor)

    /**
     *
     */
    @ExperimentalSaiseiApi
    public suspend fun toPacket(chunk: T): ByteSlice = ByteArray(8192).asSlice().writer {
        val bod = ByteArray(4096).asSlice().writer {
            marshall(chunk, this)
        }

        write(chunk.header.id, charset = Charsets.ASCII)
        writeIntLE(bod.size32)
        write(bod)
    }
}
