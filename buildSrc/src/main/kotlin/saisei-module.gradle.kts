import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `maven-publish`

    kotlin("multiplatform")
}

apply(plugin = "kotlinx-atomicfu")

group = "gay.vzt"
version = "1.0-SNAPSHOT"

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

publishing {
    repositories {
        maven("https://maven.dimensional.fun/private") {
            credentials.username = System.getenv("REPO_ALIAS")
            credentials.password = System.getenv("REPO_TOKEN")
        }
    }
}
