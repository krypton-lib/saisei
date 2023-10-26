package saisei.codec.opus

/**
 *
 */
public class OpusException : Exception {
    public val code: Int

    public constructor(code: Int) : super() {
        this.code = code
    }

    public constructor(code: Int, message: String) : super(message) {
        this.code = code
    }

    public constructor(code: Int, message: String, cause: Throwable?) : super(message, cause) {
        this.code = code
    }
}
