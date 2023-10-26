package saisei.container.wav

import kotlinx.serialization.Serializable

/**
 * Reads the header of a [WavChunk].
 *
 * @param position Position of the header in the stream.
 */
@Serializable
public data class WavChunkHeader(
    val position: Long,
    val id: String,
    val dataSize: Int,
    val headerSize: Long,
) {
    /**
     *
     */
    val dataPosition: Long get() = position + headerSize
}
