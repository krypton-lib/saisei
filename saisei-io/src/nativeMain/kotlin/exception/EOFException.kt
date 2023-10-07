package saisei.io.exception

/**
 * Exception hinting at the end of a resource, e.g. a stream.
 *
 * @constructor Create empty EOF exception
 */
public actual class EOFException : IOException {
    public actual constructor() : super()

    public actual constructor(message: String) : super(message)
}
