package saisei.io.slice.impl


import naibu.ext.size
import naibu.math.toIntSafe
import java.nio.ShortBuffer

public fun ShortMemorySlice.resizedBuffer(): ShortBuffer {
    val buffer = memory.buffer.duplicate()
    buffer.position(range.first.toIntSafe())
    buffer.limit(range.size.toIntSafe())

    return buffer
}
