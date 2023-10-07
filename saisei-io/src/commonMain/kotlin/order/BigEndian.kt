package saisei.io.order

import naibu.ext.*
import saisei.io.slice.ByteSlice
import kotlin.experimental.or

public data object BigEndian : ByteOrder() {
    override fun putShort(value: Short, output: ByteSlice) {
        output[0] = (value shr 8).asByte()
        output[1] = value.asByte()
    }

    override fun getShort(input: ByteSlice): Short {
        var v = input[1].asShort()
        v = v or (input[0].asShort() shl 8)

        return v
    }

    override fun putInt(value: Int, output: ByteSlice) {
        output[0] = (value shr 24).asByte()
        output[1] = (value shr 16).asByte()
        output[2] = (value shr 8).asByte()
        output[3] = value.asByte()
    }

    override fun getInt(input: ByteSlice): Int {
        var v = input[3].asInt()
        v = v or (input[2].asInt() shl 8)
        v = v or (input[1].asInt() shl 16)
        v = v or (input[0].asInt() shl 24)

        return v
    }

    override fun putLong(value: Long, output: ByteSlice) {
        output[7] = value.asByte()
        output[6] = (value shr 8).asByte()
        output[5] = (value shr 16).asByte()
        output[4] = (value shr 24).asByte()
        output[3] = (value shr 32).asByte()
        output[2] = (value shr 40).asByte()
        output[1] = (value shr 48).asByte()
        output[0] = (value shr 56).asByte()
    }

    override fun getLong(input: ByteSlice): Long {
        var v = input[7].asLong()
        v = v or (input[6].asLong() shl 8)
        v = v or (input[5].asLong() shl 16)
        v = v or (input[4].asLong() shl 24)
        v = v or (input[3].asLong() shl 32)
        v = v or (input[2].asLong() shl 40)
        v = v or (input[1].asLong() shl 48)
        v = v or (input[0].asLong() shl 56)

        return v
    }
}
