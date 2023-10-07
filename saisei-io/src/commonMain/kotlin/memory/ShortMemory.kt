package saisei.io.memory

public expect class ShortMemory : Memory<Short> {
    public fun load(index: Long, dst: ShortArray, at: IntRange = dst.indices)

    public fun load(index: Long, dst: ShortMemory, at: LongRange = dst.indices)

    public fun store(index: Long, src: ShortArray, from: IntRange = src.indices)

    public fun store(index: Long, src: ShortMemory, from: LongRange = src.indices)

    /**
     * View this [ShortMemory] as a [ByteMemory].
     * **Beware!** Any modifications done to the returned memory instance will be reflected in this short memory instance.
     */
    public fun bytes(): ByteMemory

    public object Allocator {
        public fun allocate(size: Int): ShortMemory

        public fun allocate(size: Long): ShortMemory

        public fun release(chunk: ShortMemory)
    }
}
