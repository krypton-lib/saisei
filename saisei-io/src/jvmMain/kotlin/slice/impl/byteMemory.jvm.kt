package saisei.io.slice.impl

import naibu.math.toIntSafe
import java.nio.ByteBuffer

public fun ByteMemorySlice.resizedBuffer(): ByteBuffer {
    val buffer = memory.buffer.duplicate()
    buffer.position(range.first.toIntSafe())
    buffer.limit(range.last.toIntSafe() + 1)

    return buffer
}
