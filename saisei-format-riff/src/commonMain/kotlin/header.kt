package saisei.io.format.riff

import saisei.io.stream.ReadStream

public data class RiffHeader(val size: UInt, val form: String)

public suspend fun readRiffHeader(stream: ReadStream): RiffHeader {
    require(stream.readID() == "RIFF") {
        "Missing 'RIFF' stream marker."
    }

    val size = stream.readUIntLE()
    val form = stream.readID()

    return RiffHeader(size, form)
}
