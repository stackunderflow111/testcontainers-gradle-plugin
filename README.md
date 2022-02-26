# Testcontainers plugin

This plugin registers [build services](https://docs.gradle.org/current/userguide/build_services.html)
which starts database containers according to user's configuration. The build services can be used to integrate
with build-time code generation tools like [gradle-jooq-plugin](https://github.com/etiennestuder/gradle-jooq-plugin)

## Usage example

See an example in the [example/build.gradle.kts](example/build.gradle.kts) file.