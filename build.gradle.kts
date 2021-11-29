plugins {
	kotlin("jvm") version "1.5.31"
	id("org.jetbrains.compose") version "1.0.0-rc4"
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/dimitree54/chnn-library")
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/dimitree54/chnn-neurons")
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}
}

@Suppress("GradlePackageUpdate")
dependencies {
	implementation(kotlin("stdlib"))
	testImplementation(kotlin("test-junit5"))
}

dependencies {
	implementation("we.rashchenko:chnn-library:v0.1.3")
	implementation("we.rashchenko:chnn-neurons:v0.1.3")

	// default dependencies for Compose app
	implementation(compose.desktop.currentOs)
}

// we need to specify following sourceSets because we store main and test not in default
//  location (which is module_path/src/main and module_path/src/test)
sourceSets.main {
	java.srcDirs("src/main")
}

sourceSets.test {
	java.srcDirs("src/test")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
	useJUnitPlatform()
}
