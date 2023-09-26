subprojects {
    if (name != "codegen") apply(plugin = "saisei-module")
}

repositories {
    mavenCentral()
}
