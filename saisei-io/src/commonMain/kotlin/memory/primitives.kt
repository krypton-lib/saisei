package saisei.io.memory

public inline fun ByteMemory.loadShort(index: Int): Short = loadShort(index.toLong())
public expect inline fun ByteMemory.loadShort(index: Long): Short

public inline fun ByteMemory.loadInt(index: Int): Int = loadInt(index.toLong())
public expect inline fun ByteMemory.loadInt(index: Long): Int

public inline fun ByteMemory.loadLong(index: Int): Long = loadLong(index.toLong())
public expect inline fun ByteMemory.loadLong(index: Long): Long

public inline fun ByteMemory.storeShort(index: Int, value: Short): Unit = storeShort(index.toLong(), value)
public expect inline fun ByteMemory.storeShort(index: Long, value: Short)

public inline fun ByteMemory.storeInt(index: Int, value: Int): Unit = storeInt(index.toLong(), value)
public expect inline fun ByteMemory.storeInt(index: Long, value: Int)

public inline fun ByteMemory.storeLong(index: Int, value: Long): Unit = storeLong(index.toLong(), value)
public expect inline fun ByteMemory.storeLong(index: Long, value: Long)


/** Fills this memory  */
public expect fun ByteMemory.fill(element: Byte, range: LongRange = indices)

/** Fills this memory  */
public expect fun ShortMemory.fill(element: Short, range: LongRange = indices)

