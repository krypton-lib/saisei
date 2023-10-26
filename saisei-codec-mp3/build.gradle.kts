import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

kotlin.sourceSets.commonMain.get().dependencies {
    implementation(libs.bundles.common)
    api(projects.saisei)
}

kotlin.targets.withType<KotlinNativeTarget>().forEach {
    it.compilations.getByName("main").cinterops.create("minimp3") {
        defFile(project.file("src/nativeInterop/cinterop/minimp3.def"))
        packageName("saisei.codec.mp3.ffi")
    }
}
