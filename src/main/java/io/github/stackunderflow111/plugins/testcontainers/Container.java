package io.github.stackunderflow111.plugins.testcontainers;

import io.github.stackunderflow111.plugins.testcontainers.Container.Params;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class Container implements BuildService<Params>, AutoCloseable {

  interface Params extends BuildServiceParameters {
    Property<String> getImageName();

    Property<Class<? extends JdbcDatabaseContainer<?>>> getContainerClass();
  }

  private static final Logger logger = LoggerFactory.getLogger(Container.class);
  private final JdbcDatabaseContainer<?> container;

  public Container() {
    Params params = getParameters();
    String imageName = params.getImageName().get();
    Class<? extends JdbcDatabaseContainer<?>> containerClass = params.getContainerClass().get();
    container = constructContainer(containerClass, imageName);
    logger.info("Starting a container with image '{}'", imageName);
    container.start();
    logger.info("Container started successfully with URL '{}'", container.getJdbcUrl());
  }

  @Override
  public void close() {
    container.stop();
  }

  public String getJdbcUrl() {
    return container.getJdbcUrl();
  }

  public String getUsername() {
    return container.getUsername();
  }

  public String getPassword() {
    return container.getPassword();
  }

  private static JdbcDatabaseContainer<?> constructContainer(
      Class<? extends JdbcDatabaseContainer<?>> clazz, String imageName) {
    try {
      return ConstructorUtils.invokeConstructor(clazz, DockerImageName.parse(imageName));
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
