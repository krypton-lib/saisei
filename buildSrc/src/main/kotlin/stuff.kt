import org.gradle.api.Project
import java.io.ByteArrayOutputStream

// Courtesy of Kord: https://github.com/kordlib/kord/tree/main/buildSrc/src/main/kotlin

internal fun Project.git(vararg command: String): String {
    val output = ByteArrayOutputStream()
    exec {
        commandLine("git", *command)
        standardOutput = output
        errorOutput = output
        workingDir = rootDir
    }.rethrowFailure().assertNormalExitValue()
    return output.toString().trim()
}

private val Project.tag
    get() = git("tag", "--no-column", "--points-at", "HEAD")
        .takeIf { it.isNotBlank() }
        ?.lines()
        ?.single()

val Project.libraryVersion
    get() = "1.0-rc.3" /*?: tag ?: run {
        val snapshotPrefix = when (val branch = git("branch", "--show-current")) {
            "master" -> providers
                .gradleProperty("nextPlannedVersion")
                .getOrElse("1.x")

            else -> branch.replace('/', '-')
        }

        "$snapshotPrefix-SNAPSHOT"
    }*/

val Project.commitHash get() = git("rev-parse", "--verify", "HEAD")

val Project.shortCommitHash get() = git("rev-parse", "--short", "HEAD")

val Project.isRelease get() = true ?: (tag != null)
