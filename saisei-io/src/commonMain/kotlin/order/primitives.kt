package saisei.io.order

import saisei.io.slice.ByteSlice

public fun ByteOrder.getFloat(slice: ByteSlice): Float = Float.fromBits(getInt(slice))

public fun ByteOrder.putFloat(value: Float, slice: ByteSlice): Unit = putInt(value.toBits(), slice)

public fun ByteOrder.getDouble(slice: ByteSlice): Double = Double.fromBits(getLong(slice))

public fun ByteOrder.putDouble(value: Double, slice: ByteSlice): Unit = putLong(value.toBits(), slice)

public fun ByteOrder.getUShort(slice: ByteSlice): UShort = getShort(slice).toUShort()

public fun ByteOrder.putUShort(value: UShort, slice: ByteSlice): Unit = putShort(value.toShort(), slice)

public fun ByteOrder.getUInt(slice: ByteSlice): UInt = getInt(slice).toUInt()

public fun ByteOrder.putUInt(value: UInt, slice: ByteSlice): Unit = putInt(value.toInt(), slice)

public fun ByteOrder.getULong(slice: ByteSlice): ULong = getLong(slice).toULong()

public fun ByteOrder.putULong(value: ULong, slice: ByteSlice): Unit = putLong(value.toLong(), slice)
