import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
}

apply(plugin = "kotlinx-atomicfu")

group = "gay.vzt"
version = "1.0-SNAPSHOT"

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
        jvmToolchain(19)

        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
    }
}
