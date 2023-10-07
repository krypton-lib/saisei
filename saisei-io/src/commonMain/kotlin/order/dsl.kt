package saisei.io.order

/** The byte-order used in network programming (big-endian). */
public typealias NetworkOrder = BigEndian

public fun ByteOrder.isLittleEndian(): Boolean = this is LittleEndian

public fun ByteOrder.isBigEndian(): Boolean = this is BigEndian
