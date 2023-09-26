import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
}

apply(plugin = "kotlinx-atomicfu")

group = "gay.vzt"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.dimensional.fun/releases")
    mavenCentral()
    maven("https://jitpack.io")
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default {
        common {
            group("native") {
                withNative()
            }
        }
    }

    mingwX64 {
    }

    jvm {
        jvmToolchain(17)

        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    sourceSets["commonMain"].dependencies {
        for (module in listOf("core", "io")) {
            implementation("naibu.stdlib:naibu-$module:1.4-RC.6")
        }

        implementation("io.github.oshai:kotlin-logging:5.0.0")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        implementation("org.jetbrains.kotlinx:atomicfu:0.22.0")
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
    }
}
