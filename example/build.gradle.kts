import io.github.stackunderflow111.plugins.testcontainers.DatabaseContainer

plugins {
    // include my plugin
    id("io.github.stackunderflow111.testcontainers")
    id("com.diffplug.spotless") version "6.7.0"
}

buildscript {
    repositories { mavenCentral() }
    dependencies {
        // provides the "org.testcontainers.containers.PostgreSQLContainer" class for my plugin
        classpath("org.testcontainers:postgresql:1.17.1")
    }
}

repositories { mavenCentral() }

val postgresContainer =
    gradle.sharedServices.registerIfAbsent("postgresContainer", DatabaseContainer::class) {
        parameters {
            // docker image name, required
            imageName.set("postgres:13-alpine")
            // testcontainers class used to create the container, required
            containerClass.set("org.testcontainers.containers.PostgreSQLContainer")
        }
    }

/**
 * Print out the jdbc URL of the postgres container started by [postgresContainer] build service.
 */
abstract class Print : DefaultTask() {

    @get:Internal abstract val container: Property<DatabaseContainer>

    @TaskAction
    fun print() {
        val jdbcUrl = container.get().jdbcUrl
        println("jdbcURL = $jdbcUrl")
    }
}

// connect the Print task to the testcontainers build service
tasks.register<Print>("printDatabaseInformation") {
    container.set(postgresContainer)
    usesService(postgresContainer)
}

spotless { kotlinGradle { ktfmt().dropboxStyle() } }
