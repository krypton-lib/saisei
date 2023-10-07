enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "saisei-root"

// ebml
include(":saisei-mkv")

include(":saisei-ebml:codegen")
include(":saisei-ebml")

// riff
include(":saisei-wav:riff")
include(":saisei-wav")

// opus
include(":saisei-opus")

//
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
    for (module in listOf("core", "io")) {
        val name = "naibu-$module"
        library(name, "naibu.stdlib", name).version("1.4-RC.8")
    }

    library("kotlin-logging", "io.github.oshai", "kotlin-logging").version("5.1.0")

    library("kotlin-atomicfu", "org.jetbrains.kotlinx", "atomicfu").version("0.22.0")
    library("kotlin-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.4.0")
    library("kotlin-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.7.3")

    //
    bundle(
        "common",
        listOf("logging", "atomicfu", "datetime", "coroutines").map { "kotlin-$it" } + "naibu-core" + "naibu-io"
    )
}
