package saisei.io.order

import naibu.ext.*
import saisei.io.slice.ByteSlice
import kotlin.experimental.or

public data object LittleEndian : ByteOrder() {
    /* 16-bit */
    override fun putShort(value: Short, output: ByteSlice) {
        output[1] // bounds check
        output[0] = value.asByte()
        output[1] = (value shr 8).asByte()
    }

    override fun getShort(input: ByteSlice): Short {
        input[1] // bounds check
        return input[0].asShort() or (input[1].asShort() shl 8)
    }

    /* 32-bit */
    override fun putInt(value: Int, output: ByteSlice) {
        output[3] // bounds check
        output[0] = value.asByte()
        output[1] = (value shr 8).asByte()
        output[2] = (value shr 16).asByte()
        output[3] = (value shr 24).asByte()
    }

    override fun getInt(input: ByteSlice): Int {
        input[3] // bounds check
        return input[0].asInt() or (input[1].asInt() shl 8) or (input[2].asInt() shl 16) or (input[3].asInt() shl 24)
    }

    /* 64-bit */
    override fun putLong(value: Long, output: ByteSlice) {
        output[0] = value.asByte()
        output[1] = (value shr 8).asByte()
        output[2] = (value shr 16).asByte()
        output[3] = (value shr 24).asByte()
        output[4] = (value shr 32).asByte()
        output[5] = (value shr 40).asByte()
        output[6] = (value shr 48).asByte()
        output[7] = (value shr 56).asByte()
    }

    override fun getLong(input: ByteSlice): Long {
        input[7] // bounds check
        return input[0].asLong() or
                (input[1].asLong() shl 8) or
                (input[2].asLong() shl 16) or
                (input[3].asLong() shl 24) or
                (input[4].asLong() shl 32) or
                (input[5].asLong() shl 40) or
                (input[6].asLong() shl 48) or
                (input[7].asLong() shl 56)
    }
}
