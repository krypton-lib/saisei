package saisei.io.order

import java.nio.ByteOrder as NioByteOrder

public val NioByteOrder.saisei: ByteOrder
    get() = if (this == NioByteOrder.LITTLE_ENDIAN) LittleEndian else BigEndian

public val ByteOrder.nio: NioByteOrder
    get() = if (this is LittleEndian) NioByteOrder.LITTLE_ENDIAN else NioByteOrder.BIG_ENDIAN

