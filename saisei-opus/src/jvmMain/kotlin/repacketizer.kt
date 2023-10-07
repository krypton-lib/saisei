package saisei.codec.opus

import club.minnced.opus.util.OpusLibrary
import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference
import saisei.io.memory.ByteMemory
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.resizedBuffer
import tomp2p.opuswrapper.Opus

public actual fun Repacketizer(): Repacketizer {
    OpusLibrary.loadFromJar()
    return RepacketizerImpl(Opus.INSTANCE.opus_repacketizer_create())
}

@JvmInline
private value class RepacketizerImpl(private val rp: PointerByReference) : Repacketizer {
    override fun reset() {
        Opus.INSTANCE.opus_repacketizer_init(rp)
    }

    override fun concat(data: ByteMemorySlice) {
        val buf = data.resizedBuffer()
        Opus.INSTANCE.opus_repacketizer_cat(rp, Native.getDirectBufferPointer(buf), buf.remaining())
    }

    override fun read(to: ByteMemory): Int {
        return Opus.INSTANCE.opus_repacketizer_out(rp, to.buffer, to.size32)
    }

    override fun close() {
        Opus.INSTANCE.opus_repacketizer_destroy(rp)
    }

    private fun throwOnError(block: () -> Int): Int {
        val code = block()
        return code.readException()?.let { throw it } ?: code
    }
}