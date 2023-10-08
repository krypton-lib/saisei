import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `maven-publish`

    kotlin("multiplatform")
}

apply(plugin = "kotlinx-atomicfu")

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()

    targetHierarchy.default {
        common {
            group("native") {
                withNative()
            }
        }
    }

    macosArm64()

    macosX64()

    mingwX64()

    linuxX64()

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
