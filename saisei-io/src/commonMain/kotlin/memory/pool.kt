package saisei.io.memory

import naibu.common.pool.Pool

/** A pool of small instances of [Memory] */
public val SmallByteMemoryPool: Pool<ByteMemory> = Pool(
    16,
    { it.fill(0) },
    { ByteMemory.Allocator.release(it) },
    { ByteMemory.Allocator.allocate(16) }
)

public val LargeByteMemoryPool: Pool<ByteMemory> = Pool(
    8,
    { it.fill(0) },
    { ByteMemory.Allocator.release(it) },
    { ByteMemory.Allocator.allocate(8192) }
)
