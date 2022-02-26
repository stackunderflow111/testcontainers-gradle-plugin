# Testcontainers plugin

This plugin registers a [build service](https://docs.gradle.org/current/userguide/build_services.html)
which starts a database container according to user's configuration. The build service can be used to integrate
with build-time code generation tool like [gradle-jooq-plugin](https://github.com/etiennestuder/gradle-jooq-plugin)

## Usage example

See an example in the [example/build.gradle.kts](example/build.gradle.kts) file.