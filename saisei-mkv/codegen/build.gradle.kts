plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.dimensional.fun/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(projects.saiseiMkv.ebml)

    // code-generation
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("dev.kord.codegen:kotlinpoet:main-SNAPSHOT")
}
