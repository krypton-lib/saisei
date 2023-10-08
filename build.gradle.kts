import org.ajoberstar.grgit.Grgit

plugins {
    id("org.ajoberstar.grgit") version "5.2.0"
}

logger.lifecycle("Version: $libraryVersion (release: $isRelease)")

allprojects {
    group = "gay.vzt.saisei"
    version = libraryVersion

    repositories {
        maven("https://maven.dimensional.fun/releases")
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "kotlinx-atomicfu")

    if (name == "codegen") {
        return@subprojects
    }

    // lol
    apply(plugin = "saisei-module")

    val repoAlias = System.getenv("REPO_ALIAS")
    val repoToken = System.getenv("REPO_TOKEN")

    if (!repoAlias.isNullOrBlank() && !repoToken.isNullOrBlank()) configure<PublishingExtension> {
        repositories {
            maven("https://maven.dimensional.fun/${if (isRelease) "release" else "snapshot"}s") {
                credentials.username = System.getenv("REPO_ALIAS")
                credentials.password = System.getenv("REPO_TOKEN")
            }
        }

        publications.withType<MavenPublication>().forEach {
            // TODO: pom
        }
    }
}
