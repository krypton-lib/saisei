package saisei.io.memory

public expect class ByteMemory : Memory<Byte> {
    public fun load(index: Long, dst: ByteArray, at: IntRange = dst.indices)

    public fun load(index: Long, dst: ByteMemory, at: LongRange = dst.indices)

    public fun store(index: Long, src: ByteArray, from: IntRange = src.indices)

    public fun store(index: Long, src: ByteMemory, from: LongRange = src.indices)

    public fun shorts(): ShortMemory

    public object Allocator {
        public fun allocate(size: Int): ByteMemory

        public fun allocate(size: Long): ByteMemory

        public fun release(chunk: ByteMemory)
    }
}
