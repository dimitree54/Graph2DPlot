plugins {
	kotlin("jvm")
	id("org.jetbrains.compose") version "0.5.0-build270"
}

repositories {
	jcenter()
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
	repositories {
		maven {
			url = uri("https://maven.pkg.github.com/dimitree54/chnn-library")
			credentials {
				username = "aaaaaaaaaa"
				password = "ghp_aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
			}
		}
	}
}

dependencies {
	// using shared code
	implementation("we.rashchenko:chnn-library:v0.1.0")

	// default dependencies for Compose app
	implementation(kotlin("stdlib"))
	implementation(compose.desktop.currentOs)
	implementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
	useJUnitPlatform()
}

task("prepareKotlinBuildScriptModel") {
}
