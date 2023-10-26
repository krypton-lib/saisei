package saisei.io.charset

import kotlin.text.Charsets

public actual object Charsets {
    public actual val UTF8: Charset = CharsetJvm(Charsets.UTF_8)

    public actual val UTF16_LE: Charset = CharsetJvm(Charsets.UTF_16LE)

    public actual val UTF16_BE: Charset = CharsetJvm(Charsets.UTF_16BE)

    public actual val ASCII: Charset = CharsetJvm(Charsets.US_ASCII)
}

