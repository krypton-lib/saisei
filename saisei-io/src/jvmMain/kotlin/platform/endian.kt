package saisei.io.platform

import naibu.platform.Platform
import java.nio.ByteOrder

/**
 * Whether this platform is little endian.
 */
public actual val Platform.isLittleEndian: Boolean
    get() = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
