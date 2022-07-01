plugins {
	id("maven-publish")
	kotlin("jvm") version "1.6.21"
	id("org.jetbrains.compose") version "1.2.0-alpha01-dev731"
}

repositories {
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("com.badlogicgames.gdx:gdx:1.11.0")
	implementation("org.jgrapht:jgrapht-core:1.5.1")

	// default dependencies for Compose app
	implementation(compose.desktop.currentOs)
}

// we need to specify following sourceSets because we store main and test not in default
//  location (which is module_path/src/main and module_path/src/test)
sourceSets.main {
	java.srcDirs("src/main")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

publishing {
	publications {
		create<MavenPublication>("default") {
			from(components["java"])
			// Include any other artifacts here, like javadocs
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/dimitree54/Graph2DPlot.git")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
