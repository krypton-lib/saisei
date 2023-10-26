package saisei.codec.mp3

import kotlinx.cinterop.*
import platform.posix.free
import saisei.codec.CodecChunkDecoder
import saisei.codec.CodecChunkEncoder
import saisei.codec.CodecParameters
import saisei.codec.mp3.ffi.mp3dec_decode_frame
import saisei.codec.mp3.ffi.mp3dec_frame_info_t
import saisei.codec.mp3.ffi.mp3dec_init
import saisei.codec.mp3.ffi.mp3dec_t
import saisei.io.slice.impl.ByteMemorySlice
import saisei.io.slice.impl.ShortMemorySlice
import saisei.io.slice.impl.asPointer
import saisei.io.slice.size32

@OptIn(ExperimentalForeignApi::class)
public val DEFAULT_MP3_DECODER_FACTORY: (CodecParameters) -> CodecChunkDecoder = { _ ->
    val ptr = cValue<mp3dec_t>()
    mp3dec_init(ptr)

    var closed = false
    object : CodecChunkDecoder {
        override fun decode(data: ByteMemorySlice, pcm: ShortMemorySlice): ShortMemorySlice = memScoped {
            require(!closed) {
                "Decoder is closed."
            }

            val info = alloc<mp3dec_frame_info_t>()
            val samples = mp3dec_decode_frame(ptr, data.asPointer?.reinterpret(), data.size32, pcm.asPointer, info.ptr)
            pcm[0..<samples * 2 * info.channels]
        }

        override fun close() {
            if (closed) return
            closed = true
            free(ptr)
        }
    }
}

public var MP3_ENCODER_FACTORY: (CodecParameters) -> CodecChunkDecoder = DEFAULT_MP3_DECODER_FACTORY

internal actual fun createMp3Decoder(codecParameters: CodecParameters): CodecChunkDecoder =
    MP3_ENCODER_FACTORY(codecParameters)

internal actual fun createMp3Encoder(codecParameters: CodecParameters): CodecChunkEncoder =
    throw UnsupportedOperationException()