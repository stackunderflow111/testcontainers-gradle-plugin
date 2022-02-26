package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.provider.Property;

public abstract class TestcontainersExtension {
    /** The docker image name, such as "postgres:13" */
    public abstract Property<String> getImageName();
    /** The container class name, such as "org.testcontainers.containers.PostgreSQLContainer" */
    public abstract Property<String> getContainerClass();
}
