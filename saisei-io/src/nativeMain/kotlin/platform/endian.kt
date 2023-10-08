package saisei.io.platform

import naibu.platform.Platform
import kotlin.experimental.ExperimentalNativeApi

/**
 * Whether this platform is little endian.
 */
@OptIn(ExperimentalNativeApi::class)
public actual val Platform.isLittleEndian: Boolean get() = kotlin.native.Platform.isLittleEndian
