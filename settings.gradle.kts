enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "saisei-root"

// container formats
include(":saisei-container-mkv")
include(":saisei-container-wav")

// binary formats
include(":saisei-format-ebml", ":saisei-format-ebml:codegen")
include(":saisei-format-riff")

// codecs
include(":saisei-codec-mp3")
include(":saisei-codec-aac")
include(":saisei-codec-opus")
include(":saisei-codec-vorbis")

include(":saisei-codecs-lavaplayer") // Codec implementations for the JVM backed by lavaplayer natives.

// core libraries
include(":saisei-io")
include(":saisei")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            common()
        }
    }
}

fun VersionCatalogBuilder.common() {
    //
    library("naibu-core", "naibu.stdlib", "naibu-core").version("1.4-RC.8")

    library("kotlin-logging", "io.github.oshai", "kotlin-logging").version("5.1.0")

    library("kotlin-atomicfu", "org.jetbrains.kotlinx", "atomicfu").version("0.22.0")
    library("kotlin-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.4.0")
    library("kotlin-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.7.3")

    //
    bundle(
        "common",
        listOf("logging", "atomicfu", "datetime", "coroutines").map { "kotlin-$it" } + "naibu-core"
    )
}
