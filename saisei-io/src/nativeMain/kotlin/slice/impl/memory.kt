package saisei.io.slice.impl

import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
public val ByteMemorySlice.asPointer: CPointer<ByteVar>?
    get() = memory.pointer + range.first

@OptIn(ExperimentalForeignApi::class)
public val ShortMemorySlice.asPointer: CPointer<ShortVar>?
    get() = memory.pointer + range.first
