package saisei.io.format.ebml.element

import naibu.cio.stream.read.SeekableReadStream
import naibu.cio.stream.write.WriteStream
import naibu.io.toByteArray
import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.readVariableEBMLInteger

data class ElementHeader(
    /**
     * The byte position of this element in the stream.
     */
    val headerPosition: Long,
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
    val dataPosition: Long get() = headerPosition + headerSize

    /**
     *
     */
    val size: Long get() = headerSize + dataSize

    suspend fun jumpTo(stream: SeekableReadStream) {
        stream.seek(headerPosition)
    }

    suspend fun jumpToData(stream: SeekableReadStream) {
        stream.seek(dataPosition)
    }

    suspend fun skipDataFully(stream: SeekableReadStream) {
        stream.discardFully(dataSize)
    }

    /**
     *
     */
    fun calculateRemaining(currentPos: Long) = headerPosition + headerSize + dataSize - currentPos

    companion object {
        suspend fun read(stream: SeekableReadStream): ElementHeader {
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

        suspend fun write(stream: WriteStream, value: ElementHeader) {
            stream.write(value.id.code.toByteArray())
            TODO()
        }
    }
}
