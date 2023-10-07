plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val kotlinVersion = "1.9.10"
dependencies {
    implementation(kotlin("gradle-plugin", version = kotlinVersion))
    implementation(kotlin("serialization", version = kotlinVersion))
    implementation("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.22.0")

    // groovy shit
    implementation(gradleApi())
    implementation(localGroovy())
}