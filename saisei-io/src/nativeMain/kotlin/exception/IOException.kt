package saisei.io.exception

/**
 * @constructor Create empty IO exception
 */
public actual open class IOException : Exception {
    public actual constructor() : super()

    public actual constructor(message: String) : super(message)

    public actual constructor(message: String, cause: Throwable?) : super(message, cause)
}
