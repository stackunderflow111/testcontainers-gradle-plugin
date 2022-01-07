plugins {
    id("groovy")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.19.0"
    id("com.diffplug.spotless") version "6.1.2"
}

group = "io.github.stackunderflow111"
version = project.findProperty("version") ?: "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.testcontainers:jdbc:1.16.2")
    implementation("org.flywaydb:flyway-core:8.4.0")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0") {
        exclude(group = "org.codehaus.groovy")
    }
}

gradlePlugin {
    plugins {
        create("testcontainersPlugin") {
            id = "io.github.stackunderflow111.testcontainers"
            displayName = "Testconatiners plugin"
            description = "Configure an existing task to run testcontainers in its doFirst block"
            implementationClass = "io.github.stackunderflow111.TestcontainersPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/stackunderflow111/testcontainers-plugin"
    vcsUrl = "https://github.com/stackunderflow111/testcontainers-plugin.git"
    tags = listOf("testcontainers", "jooq", "flyway")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()
    }
    kotlinGradle {
        ktlint()
    }
}
