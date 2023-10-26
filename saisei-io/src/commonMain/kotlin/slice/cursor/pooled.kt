package saisei.io.slice.cursor

//import naibu.common.Closeable
//import naibu.common.pool.Pool
//import saisei.io.memory.ByteMemory
//import saisei.io.slice.ByteSlice
//import saisei.io.slice.impl.asSlice

//public class PooledByteMemorySlice(
//    private val memory: ByteMemory,
//    range: LongRange,
//    private val pool: Pool<ByteMemory>,
//) : ByteSlice by memory.asSlice(range), Closeable {
//    override fun close() {
//        pool.give(memory)
//    }
//}
