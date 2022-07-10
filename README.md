# Testcontainers plugin

This plugin registers [build services](https://docs.gradle.org/current/userguide/build_services.html)
which starts database containers according to user's configuration. The build services can be used to integrate
with build-time code generation tools like [gradle-jooq-plugin](https://github.com/etiennestuder/gradle-jooq-plugin)

## Usage example

### Basic example

```kotlin
plugins {
    // include my plugin
    id("io.github.stackunderflow111.testcontainers") version "<latest version>"
}

buildscript {
    repositories { mavenCentral() }
    dependencies {
        // provides the "org.testcontainers.containers.PostgreSQLContainer" class for my plugin
        classpath("org.testcontainers:postgresql:1.16.3")
    }
}

val postgresContainer =
    gradle.sharedServices.registerIfAbsent("postgresContainer", DatabaseContainerBuildService::class) {
        parameters {
            // docker image name, required
            imageName.set("postgres:13-alpine")
            // testcontainers class used to create the container, required
            containerClass.set("org.testcontainers.containers.PostgreSQLContainer")
        }
    }

```

### Full examples

You can see an example in the [example/build.gradle.kts](example/build.gradle.kts) file. The 
[jooq-flyway-testcontainers-demo](https://github.com/stackunderflow111/jooq-flyway-testcontainers-demo) project
is an example integrating this plugin with flyway and Jooq.
