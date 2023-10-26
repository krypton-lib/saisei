package saisei.io.format.riff

import saisei.io.charset.Charsets
import saisei.io.charset.decodeIntoString
import saisei.io.order.LittleEndian
import saisei.io.slice.cursor.ByteWriteCursor
import saisei.io.slice.cursor.write
import saisei.io.stream.*

public suspend fun ReadStream.readIntLE(): Int = readInt(LittleEndian)

public suspend fun ReadStream.readUIntLE(): UInt = readUInt(LittleEndian)

public suspend fun ReadStream.readShortLE(): Short = readShort(LittleEndian)

public suspend fun ReadStream.readUShortLE(): UShort = readUShort(LittleEndian)

public suspend fun ReadStream.readID(): String = readFully(4).decodeIntoString(charset = Charsets.ASCII)

public fun ByteWriteCursor.writeIntLE(value: Int): Unit = writeInt(value, LittleEndian)

public fun ByteWriteCursor.writeUIntLE(value: UInt): Unit = writeUInt(value, LittleEndian)

public fun ByteWriteCursor.writeShortLE(value: Short): Unit = writeShort(value, LittleEndian)

public fun ByteWriteCursor.writeUShortLE(value: UShort): Unit = writeUShort(value, LittleEndian)

public fun ByteWriteCursor.writeID(value: String): Unit {
    write(value, charset = Charsets.ASCII)
}
