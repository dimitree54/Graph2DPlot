plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.3.1"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    // using shared code
    implementation(project(":library"))

    // default dependencies for Compose app
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)
}
