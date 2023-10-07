package saisei.io.stream

import java.io.InputStream

public inline fun ReadStream(value: InputStream): SeekableReadStream = InputReadStream(value)
