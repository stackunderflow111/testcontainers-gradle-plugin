package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.NamedDomainObjectContainer;

public interface TestcontainersExtension {
    NamedDomainObjectContainer<DatabaseContainerConfig> getDatabaseContainerBuildServices();
}
