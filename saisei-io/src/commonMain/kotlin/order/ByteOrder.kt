package saisei.io.order

import naibu.platform.Platform
import saisei.io.platform.isLittleEndian
import saisei.io.slice.ByteSlice

public sealed class ByteOrder {
    public companion object {
        /** The current byte order used by the underlying system. */
        public val Native: ByteOrder get() = if (Platform.isLittleEndian) LittleEndian else BigEndian
    }

    /* shorts */
    public abstract fun putShort(value: Short, output: ByteSlice)
    public abstract fun getShort(input: ByteSlice): Short

    /* ints */
    public abstract fun putInt(value: Int, output: ByteSlice)
    public abstract fun getInt(input: ByteSlice): Int

    /* longs */
    public abstract fun putLong(value: Long, output: ByteSlice)
    public abstract fun getLong(input: ByteSlice): Long
}
