import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

kotlin.sourceSets.commonMain.get().dependencies {
    implementation(libs.bundles.common)
    api(projects.saisei)
}

kotlin.sourceSets["jvmMain"].dependencies {
    implementation("club.minnced:opus-java:1.1.1")
    implementation("net.java.dev.jna:jna:4.4.0")
}

listOf("mingwX64", "linuxX64", "macosX64", "macosArm64", "macos_x64", "macos_arm64")
    .mapNotNull(kotlin.targets::findByName)
    .filterIsInstance<KotlinNativeTarget>()
    .forEach {
        val main by it.compilations
        main.cinterops.create("libopus") {
            defFile(project.file("src/nativeInterop/cinterop/libopus.def"))
            packageName("saisei.codec.opus.ffi")
        }
    }
