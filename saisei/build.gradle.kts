kotlin.sourceSets["commonMain"].dependencies {
    implementation(projects.saiseiMkv)
}

kotlin.sourceSets["jvmTest"].dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation("club.minnced:opus-java:1.1.1")

    implementation("dev.kord:kord-core-voice:0.11.0")

    compileOnly("net.java.dev.jna:jna:4.4.0")
}
