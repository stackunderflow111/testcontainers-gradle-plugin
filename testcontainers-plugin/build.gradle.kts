plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.6.10"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
    id("com.diffplug.spotless") version "6.8.0"
}

group = "io.github.stackunderflow111"

version = project.findProperty("version") ?: "0.1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation("org.testcontainers:jdbc:1.17.3")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

gradlePlugin {
    plugins {
        create("testcontainersPlugin") {
            id = "io.github.stackunderflow111.testcontainers"
            displayName = "Testcontainers plugin"
            description = "Testcontainers build service"
            implementationClass =
                "io.github.stackunderflow111.plugins.testcontainers.TestcontainersPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/stackunderflow111/testcontainers-plugin"
    vcsUrl = "https://github.com/stackunderflow111/testcontainers-plugin.git"
    tags = listOf("testcontainers")
}

spotless {
    kotlinGradle { ktfmt().dropboxStyle() }
    kotlin { ktfmt() }
}
