subprojects {
    if (name !in listOf("test", "codegen")) apply(plugin = "saisei-module")
    apply(plugin = "kotlinx-atomicfu")

    group = "gay.vzt"
    version = "1.0"

    repositories {
        maven("https://maven.dimensional.fun/releases")

        maven("https://maven.dimensional.fun/private") {
            credentials.username = System.getenv("REPO_ALIAS")
            credentials.password = System.getenv("REPO_TOKEN")
        }

        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}
