package saisei.io.exception

/**
 * Exception hinting at the end of a resource, e.g. a stream.
 *
 * @constructor Create empty EOF exception
 */
public expect class EOFException : IOException {
    public constructor()

    public constructor(message: String)
}
