package saisei.codecs.mp3

import com.sedmelluq.discord.lavaplayer.natives.mp3.Mp3DecoderLibrary
import com.sedmelluq.lava.common.natives.NativeResourceHolder
import saisei.codec.CodecChunkDecoder
import saisei.codec.mp3.Mp3NeedMoreDataException
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.resizedBuffer

internal class Mp3Decoder : CodecChunkDecoder, NativeResourceHolder() {
    private val library = Mp3DecoderLibrary.INSTANCE
    private val instance = library.create()

    override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice {
        checkNotReleased()

        val src = data.resizedBuffer()
        val dst = pcm.resizedBuffer()

        require(src.isDirect && dst.isDirect) { "Arguments must be direct buffers." }

        var result = library.decode(instance, src, src.remaining(), dst, dst.remaining() * 2)
        while (result == ERROR_NEW_FORMAT) {
            result = library.decode(instance, src, 0, dst, dst.remaining() * 2)
        }

        if (result == ERROR_NEED_MORE) {
            throw Mp3NeedMoreDataException()
        } else check(result >= 0) {
            "Decoding failed with error $result"
        }

        return pcm[0..<result / 2]
    }

    override fun close() {
        super<NativeResourceHolder>.close()
    }

    override fun freeResources() {
        library.destroy(instance)
    }

    companion object {
        internal const val ERROR_NEED_MORE = -10
        internal const val ERROR_NEW_FORMAT = -11
    }
}
