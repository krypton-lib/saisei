kotlin.sourceSets["commonMain"].dependencies {
    implementation(libs.bundles.common)
    api(projects.saisei)
}

kotlin.sourceSets["jvmMain"].dependencies {
    implementation("club.minnced:opus-java:1.1.1")
    implementation("net.java.dev.jna:jna:4.4.0")
}
