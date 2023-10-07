package saisei.io.stream

/**
 * Creates a [ReadStream] using this byte array.
 */
public fun ByteArray.readStream(): ReadStream = ReadStream(this)

/**
 * Creates a [ReadStream] from the given [ByteArray].
 *
 * @param array The array to read from.
 */
public fun ReadStream(array: ByteArray): SeekableReadStream = ByteArrayReadStream(array)

