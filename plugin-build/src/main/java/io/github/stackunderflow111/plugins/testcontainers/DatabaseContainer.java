package io.github.stackunderflow111.plugins.testcontainers;

import io.github.stackunderflow111.plugins.testcontainers.DatabaseContainer.Params;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

public abstract class DatabaseContainer implements BuildService<Params>, AutoCloseable {

    public abstract static class Params implements BuildServiceParameters {
        public abstract Property<String> getImageName();

        public abstract Property<String> getContainerClass();
    }

    private static final Logger logger = LoggerFactory.getLogger(DatabaseContainer.class);
    private final JdbcDatabaseContainer<?> databaseContainer;

    public DatabaseContainer() {
        Params params = getParameters();
        String imageName = params.getImageName().get();
        String containerClassName = params.getContainerClass().get();
        Class<? extends JdbcDatabaseContainer<?>> containerClass =
                findContainerClass(containerClassName);
        databaseContainer = constructContainer(containerClass, imageName);
        logger.info("Starting a container with image '{}'", imageName);
        databaseContainer.start();
        logger.info("Container started successfully with URL '{}'", databaseContainer.getJdbcUrl());
    }

    @Override
    public void close() {
        databaseContainer.stop();
    }

    public String getJdbcUrl() {
        return databaseContainer.getJdbcUrl();
    }

    public String getUsername() {
        return databaseContainer.getUsername();
    }

    public String getPassword() {
        return databaseContainer.getPassword();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends JdbcDatabaseContainer<?>> findContainerClass(
            String containerClassName) {
        try {
            return (Class<? extends JdbcDatabaseContainer<?>>)
                    Class.forName(containerClassName).asSubclass(JdbcDatabaseContainer.class);
        } catch (ClassNotFoundException e) {
            throw new InvalidUserDataException("Could find the class " + containerClassName, e);
        }
    }

    private static JdbcDatabaseContainer<?> constructContainer(
            Class<? extends JdbcDatabaseContainer<?>> clazz, String imageName) {
        try {
            return ConstructorUtils.invokeConstructor(clazz, imageName);
        } catch (Exception e) {
            throw new InvalidUserDataException(
                    "Could not create a new instance with for class "
                            + clazz.getName()
                            + " with image "
                            + imageName,
                    e);
        }
    }
}
