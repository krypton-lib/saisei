package saisei.codec.opus

import java.util.*
import kotlin.jvm.optionals.getOrNull

private val library: NativeOpusLibrary? by lazy {
    ServiceLoader.load(NativeOpusLibrary::class.java)
        .findFirst()
        .getOrNull()
}

internal actual fun getOpusLibrary(): NativeOpusLibrary =
    library ?: error("Unable to find Opus library implementation")
