# Testcontainers plugin

This plugin is mainly used to integrate the [gradle-jooq-plugin](https://github.com/etiennestuder/gradle-jooq-plugin)
with testcontainers. 

## Usage example

```kotlin
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Configuration

plugins {
    // include my plugin
    id("github.stackunderflow111.testcontainers") version "<latest version>"
    // include the jooq gradle plugin
    id("nu.studer.jooq") version "6.0.1"
}

group = "github.stackunderflow111"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    jooqGenerator("org.postgresql:postgresql:42.3.1")
    // the testcontainersRuntime configuration block is used to configure testcontainers dependencies
    // here testcontainers need the postgres module , and flyway needs the postgres connector
    testcontainersRuntime("org.testcontainers:postgresql:1.16.2")
    testcontainersRuntime("org.postgresql:postgresql:42.3.1")
}

// jooq configuration
jooq {
    configurations {
        create("main") {  // name of the jOOQ configuration
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                }
            }
        }
    }
}

// testcontainers configuration block
testcontainers {
    // docker image name, required
    imageName.set("postgres:13")
    // testcontainers class used to create the container, required
    containerClass.set("org.testcontainers.containers.PostgreSQLContainer")
    // configure the container before it starts, not required (default works fine)
    configureContainer {
        withUsername("stackunderflow")
    }
    // the task to configure, required
    task = tasks.named<JooqGenerate>("generateJooq")
    // add a flyway migrate step to the doFirst block of the "generateJooq" task
    flywayMigrateStep {
        // configure flyway before migration runs
        configureFlyway {
            // usually we specify the "locations" property, otherwise flyway doesn't know where to look for migrations
            locations("filesystem:src/main/resources/db/migration")
        }
    }
    // add a custom step (after the previously configured flyway step) to the doFirst block of the "generateJooq" task
    customActionStep {
        // the action to run. "this" is the "generateJooq" task, and "container" is the started container
        run { container ->
            this as JooqGenerate
            // The jooq plugin doesn't allow modification to the "jooqConfiguration" field, so we have to use reflection
            val jooqConfigurationField = JooqGenerate::class.java.getDeclaredField("jooqConfiguration")
            jooqConfigurationField.isAccessible = true
            val jooqConfiguration = jooqConfigurationField.get(this) as Configuration
            jooqConfiguration.jdbc.apply {
                url = container.jdbcUrl
                user = container.username
                password = container.password
            }
        }
    }
}
```