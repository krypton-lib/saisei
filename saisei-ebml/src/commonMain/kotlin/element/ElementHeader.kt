package saisei.io.format.ebml.element

import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.readVariableEBMLInteger
import saisei.io.stream.SeekableReadStream

public data class ElementHeader(
    /**
     * The byte position of this element in the stream.
     */
    val position: Long,
    /**
     * The element's ID.
     */
    val id: EbmlID,
    /**
     * The data size of the Element.
     */
    val dataSize: Long,
    /**
     * The header size of the Element.
     */
    val headerSize: Long,
) {
    /**
     * The position of this element's data.
     */
    val dataPosition: Long get() = position + headerSize

    /**
     *
     */
    val size: Long get() = headerSize + dataSize

    public suspend fun jumpTo(stream: SeekableReadStream) {
        if (stream.position != position) stream.seek(position)
    }

    public suspend fun jumpToData(stream: SeekableReadStream) {
        if (stream.position != dataPosition) stream.seek(dataPosition)
    }

    public suspend fun skip(stream: SeekableReadStream) {
        val remaining = calculateRemaining(stream.position)
        if (remaining > 0) stream.discardFully(remaining)
    }

    /**
     *
     */
    public fun calculateRemaining(currentPos: Long): Long = position + size - currentPos

    public companion object {
        public suspend fun read(stream: SeekableReadStream): ElementHeader {
            /* set the starting position of this element. */
            val position = stream.position

            // read the id & data size
            val idCode = stream.readVariableEBMLInteger()
            val id = EbmlID.Coded(idCode, "$idCode")
            val dataSize = stream.readVariableEBMLInteger()

            // set the size of the header.
            val headerSize = stream.position - position

            // build the header
            return ElementHeader(position, id, dataSize, headerSize)
        }
    }
}
