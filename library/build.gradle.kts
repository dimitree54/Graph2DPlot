plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

/*
kotlin {
    org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
    sourceSets.main {
        java.srcDirs("src/main/myJava", "src/main/myKotlin")
    }
}
*/
sourceSets.main {
    java.srcDirs("src")
}
