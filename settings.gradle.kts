pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
	}

}
rootProject.name = "ChNN"
include(":library")
include(":client")
