package saisei.io.charset

import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice


/**
 * Decodes this byte array into a string using the specified [charset].
 */
public fun ByteArray.decodeIntoString(range: IntRange = indices, charset: Charset = Charsets.UTF8): String =
    charset.decode(this, range)

/**
 * Decodes this [ByteMemory] instance into a string using the specified [charset].
 */
public fun ByteMemory.decodeIntoString(range: LongRange = indices, charset: Charset = Charsets.UTF8): String =
    charset.decode(this, range)

/**
 * Decodes this [ByteSlice] instance into a string using the specified [charset].
 */
public fun ByteSlice.decodeIntoString(charset: Charset = Charsets.UTF8): String =
    charset.decode(this)

/**
 * Encodes this string into a byte array using the specified [charset].
 */
public fun String.encodeIntoByteArray(range: IntRange = indices, charset: Charset = Charsets.UTF8): ByteArray =
    charset.encode(this, range)


