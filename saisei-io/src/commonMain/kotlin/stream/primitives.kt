package saisei.io.stream

import naibu.ext.asInt
import naibu.logging.logging
import saisei.io.exception.EOFException
import saisei.io.order.ByteOrder
import saisei.io.order.NetworkOrder
import saisei.io.slice.impl.asSlice

private val log by logging("saisei.io.stream.ReadStream")

/**
 *
 */
public suspend fun ReadStream.readFully(n: Int): ByteArray {
    val dst = ByteArray(n)
    readFully(dst)

    return dst
}

/**
 * Reads a single byte from the stream, or returns `null` if the stream has encountered an EOF exception.
 */
public suspend fun ReadStream.readOrNull(): Byte? = try {
    read()
} catch (eof: EOFException) {
    log.debug(eof) { "EOF while reading byte" }
    null
}

public suspend fun ReadStream.readAsInt(): Int =
    readOrNull()?.asInt() ?: -1

// SHORT
public suspend fun ReadStream.readShort(order: ByteOrder = NetworkOrder): Short =
    order.getShort(readFully(2).asSlice())

public suspend fun ReadStream.readUShort(order: ByteOrder = NetworkOrder): UShort =
    readShort(order).toUShort()

// INT
public suspend fun ReadStream.readInt(order: ByteOrder = NetworkOrder): Int =
    order.getInt(readFully(4).asSlice())

public suspend fun ReadStream.readUInt(order: ByteOrder = NetworkOrder): UInt =
    readInt(order).toUInt()

// LONG
public suspend fun ReadStream.readLong(order: ByteOrder = NetworkOrder): Long =
    order.getLong(readFully(8).asSlice())

public suspend fun ReadStream.readULong(order: ByteOrder = NetworkOrder): ULong =
    readLong(order).toULong()

// FLOATING POINT
public suspend fun ReadStream.readFloat(order: ByteOrder = NetworkOrder): Float =
    Float.fromBits(readInt(order))

public suspend fun ReadStream.readDouble(order: ByteOrder = NetworkOrder): Double =
    Double.fromBits(readLong(order))
