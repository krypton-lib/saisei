package saisei.io.charset

import kotlin.text.Charsets

public actual object Charsets {
    public actual val UTF8: Charset = CharsetJvm(Charsets.UTF_8)

    public actual val ASCII: Charset = CharsetJvm(Charsets.US_ASCII)
}

