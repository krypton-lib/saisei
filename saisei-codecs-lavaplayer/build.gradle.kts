kotlin.sourceSets.commonMain.get().dependencies {
    api(projects.saiseiCodecAac)

    api(projects.saiseiCodecMp3)

    api(projects.saiseiCodecOpus)

    api(projects.saiseiCodecVorbis)
}

kotlin.sourceSets.jvmMain.get().dependencies {
    implementation("dev.arbjerg:lavaplayer-natives:2.0.2")

    implementation("dev.arbjerg:lava-common:2.0.2")
}
