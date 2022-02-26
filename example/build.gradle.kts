import io.github.stackunderflow111.plugins.testcontainers.DatabaseContainer

plugins {
    // include my plugin
    id("io.github.stackunderflow111.testcontainers")
    id("com.diffplug.spotless") version "6.3.0"
}

buildscript {
    repositories { mavenCentral() }
    dependencies {
        // provides the "org.testcontainers.containers.PostgreSQLContainer" class
        classpath("org.testcontainers:postgresql:1.16.3")
    }
}

repositories { mavenCentral() }

// testcontainers configuration block
testcontainers {
    // docker image name, required
    imageName.set("postgres:13-alpine")
    // testcontainers class used to create the container, required
    containerClass.set("org.testcontainers.containers.PostgreSQLContainer")
}

val buildServiceRegistrations: NamedDomainObjectSet<BuildServiceRegistration<*, *>> =
    gradle.sharedServices.registrations

// retrieve the testcontainers build service registered by my plugin
@Suppress("UNCHECKED_CAST")
val postgresProvider =
    buildServiceRegistrations.getByName("testcontainers").service as Provider<DatabaseContainer>

/** Print out the jdbc URL of the postgres container started by [postgresProvider] build service. */
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
    container.set(postgresProvider)
    usesService(postgresProvider)
}

spotless { kotlinGradle { ktfmt().dropboxStyle() } }
