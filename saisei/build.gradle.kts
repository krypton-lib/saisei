kotlin.sourceSets["commonMain"].dependencies {
    implementation(libs.bundles.common)
    api(projects.saiseiMkv)
    api(projects.saiseiIo)
}
