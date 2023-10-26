package saisei.codecs

import com.sedmelluq.discord.lavaplayer.natives.mp3.Mp3DecoderLibrary
import com.sedmelluq.lava.common.natives.NativeLibraryLoader
import com.sedmelluq.lava.common.natives.architecture.DefaultOperatingSystemTypes

internal val loaders = listOf(
    NativeLibraryLoader.createFiltered(Mp3DecoderLibrary::class.java, "libmpg123-0") {
        it.osType == DefaultOperatingSystemTypes.WINDOWS
    },
    NativeLibraryLoader.create(Mp3DecoderLibrary::class.java, "connector")
)

internal fun loadNatives() {
    loaders.forEach(NativeLibraryLoader::load)
}
