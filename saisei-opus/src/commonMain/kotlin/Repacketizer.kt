package saisei.codec.opus

import naibu.common.Closeable
import naibu.common.Resetable
import saisei.io.memory.ByteMemory
import saisei.io.slice.impl.ByteMemorySlice

public interface Repacketizer : Resetable, Closeable {
    /**
     *
     */
    public fun concat(data: ByteMemorySlice)

    /**
     *
     */
    public fun read(to: ByteMemory): Int
}
