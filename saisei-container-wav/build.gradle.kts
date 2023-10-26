kotlin.sourceSets["commonMain"].dependencies {
    implementation(libs.bundles.common)
    api(projects.saisei)
    api(projects.saiseiIo)
    api(projects.saiseiFormatRiff)
}
