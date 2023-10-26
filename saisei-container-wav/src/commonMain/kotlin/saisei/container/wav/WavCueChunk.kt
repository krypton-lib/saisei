package saisei.container.wav

import kotlinx.serialization.Serializable
import saisei.io.format.riff.readID
import saisei.io.format.riff.readIntLE
import saisei.io.format.riff.writeID
import saisei.io.format.riff.writeIntLE
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.stream.ReadStream
import saisei.io.stream.SeekableReadStream

@Serializable
public data class WavCueChunk(
    override val header: WavChunkHeader,
    val dwCuePoints: Int,
    val points: List<Point>,
) : WavChunk() {
    @Serializable
    public data class Point(
        /**
         * The dwIdentifier field contains a unique number (ie, different than the ID number of any other CuePoint
         * structure).
         *
         * This is used to associate a CuePoint structure with other structures used in other chunks which will be
         * described later.
         */
        val dwIdentifier: Int,

        /**
         * Specifies the position of the cue point within the "play order" (as determined by the Playlist chunk.
         * See that chunk for a discussion of the play order).
         */
        val dwPosition: Int,

        /**
         * Specifies the chunk ID of the Data or Wave List chunk which actually contains the waveform data to
         * which this CuePoint refers.
         *
         * If there is only one Data chunk in the file, then this field is set to the ID 'data'. On the other hand,
         * if the file contains a Wave List (which can contain both 'data' and 'slnt' chunks), then fccChunk will
         * specify 'data' or 'slnt' depending upon in which type of chunk the referenced waveform data is found.
         */
        val fccChunk: String,

        /**
         * Specifies the byte offset of the start of the 'data' or 'slnt' chunk which actually contains
         * the waveform data to which this CuePoint refers
         *
         * This offset is relative to the start of the first chunk within the Wave List. (ie, It's the byte offset,
         * within the Wave List, of where the 'data' or 'slnt' chunk of interest appears.
         *
         * The first chunk within the List would be at an offset of 0).
         */
        val dwChunkStart: Int,

        /**
         * Specifies the byte offset of the start of the block containing the position. This offset
         * is relative to the start of the waveform data within the 'data' or 'slnt' chunk.
         */
        val dwBlockStart: Int,

        /**
         * Specifies the sample offset of the cue point relative to the start of the block.
         *
         * In an uncompressed file, this equates to simply being the offset within the waveformData
         * array.
         *
         * Unfortunately, the WAVE documentation is much too ambiguous, and doesn't define what it means
         * by the term "sample offset". This could mean a byte offset, or it could mean counting the sample
         * points (for example, in a 16-bit wave, every 2 bytes would be 1 sample point), or it could even
         * mean sample frames (as the loop offsets in AIFF are specified).
         *
         * Who knows? The guy who conjured up the Cue chunk certainly isn't saying. I'm assuming that it's
         * a byte offset, like the above 2 fields.
         */
        val dwSampleOffset: Int,
    ) {
        public companion object {
            public suspend fun unmarshall(stream: ReadStream): Point = Point(
                stream.readIntLE(),
                stream.readIntLE(),
                stream.readID(),
                stream.readIntLE(),
                stream.readIntLE(),
                stream.readIntLE(),
            )

            public fun marshall(value: Point, cursor: ByteWriteCursor) {
                cursor.writeIntLE(value.dwIdentifier)
                cursor.writeIntLE(value.dwPosition)
                cursor.writeID(value.fccChunk)
                cursor.writeIntLE(value.dwChunkStart)
                cursor.writeIntLE(value.dwBlockStart)
                cursor.writeIntLE(value.dwSampleOffset)
            }
        }
    }

    override fun toString(): String {
        return "WavCueChunk(${header.id}, ${header.dataSize}; dwCuePoints=$dwCuePoints)"
    }

    public companion object : WavChunkMarshallFactory<WavCueChunk>() {
        override val id: String get() = "cue "

        override suspend fun unmarshall(header: WavChunkHeader, stream: SeekableReadStream): WavCueChunk {
            val dwCuePoints = stream.readIntLE()
            return WavCueChunk(
                header,
                dwCuePoints,
                List(dwCuePoints) { Point.unmarshall(stream) }
            )
        }

        override suspend fun marshall(value: WavCueChunk, cursor: ByteWriteCursor) {
            cursor.writeIntLE(value.dwCuePoints)
            for (point in value.points) Point.marshall(point, cursor)
        }
    }
}
