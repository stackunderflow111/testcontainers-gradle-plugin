package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.provider.Property;

public abstract class DatabaseContainerConfig {
    private final String name;

    public DatabaseContainerConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    /** The docker image name, such as "postgres:13" */
    public abstract Property<String> getImageName();
    /** The container class name, such as "org.testcontainers.containers.PostgreSQLContainer" */
    public abstract Property<String> getContainerClass();
}
