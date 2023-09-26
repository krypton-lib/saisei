enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "saisei-root"

// ebml
include(":saisei-mkv:codegen")
include(":saisei-mkv:ebml")
include(":saisei-mkv")

// riff
include(":saisei-wav:riff")
include(":saisei-wav")


//
include(":saisei-io")
include(":saisei")
