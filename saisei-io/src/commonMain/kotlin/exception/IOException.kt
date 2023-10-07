package saisei.io.exception

/**
 * @constructor Create empty IO exception
 */
public expect open class IOException : Exception {
    public constructor()

    public constructor(message: String)

    public constructor(message: String, cause: Throwable?)
}
