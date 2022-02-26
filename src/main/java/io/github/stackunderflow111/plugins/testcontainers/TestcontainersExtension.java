package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.provider.Property;
import org.testcontainers.containers.JdbcDatabaseContainer;

interface TestcontainersExtension {
  /** The docker image name, such as "postgres:13" */
  Property<String> getImageName();
  /** The container class name, such as "org.testcontainers.containers.PostgreSQLContainer" */
  Property<Class<? extends JdbcDatabaseContainer<?>>> getContainerClass();
}
