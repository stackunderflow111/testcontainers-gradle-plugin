package github.stackunderflow111.utils;

import java.lang.reflect.Constructor;
import org.gradle.api.InvalidUserDataException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerUtils {
  public static JdbcDatabaseContainer<?> initContainer(
      String imageName, String containerClass, ClassLoader classLoader) {
    Constructor<JdbcDatabaseContainer<?>> constructor =
        getContainerConstructor(containerClass, classLoader);
    return constructContainer(constructor, imageName);
  }

  public static Runnable getContainerStopHook(GenericContainer<?> container) {
    return () -> {
      if (!container.isShouldBeReused()) {
        container.stop();
      }
    };
  }

  private static Constructor<JdbcDatabaseContainer<?>> getContainerConstructor(
      String containerClass, ClassLoader classLoader) {
    Class<JdbcDatabaseContainer<?>> clazz;
    try {
      clazz =
          (Class<JdbcDatabaseContainer<?>>)
              Class.forName(containerClass, true, classLoader)
                  .asSubclass(JdbcDatabaseContainer.class);
    } catch (ClassNotFoundException e) {
      throw new InvalidUserDataException(
          "class " + containerClass + " not found, check your 'containerClass' property", e);
    }
    Constructor<JdbcDatabaseContainer<?>> constructor;
    try {
      constructor = clazz.getConstructor(DockerImageName.class);
    } catch (NoSuchMethodException e) {
      throw new InvalidUserDataException("Cannot find the suitable constructor for " + clazz, e);
    }
    return constructor;
  }

  private static JdbcDatabaseContainer<?> constructContainer(
      Constructor<JdbcDatabaseContainer<?>> constructor, String imageName) {
    JdbcDatabaseContainer<?> container;
    try {
      container = constructor.newInstance(DockerImageName.parse(imageName));
    } catch (Exception e) {
      throw new InvalidUserDataException(
          "Could not create a new instance with constructor "
              + constructor.getName()
              + " with image "
              + imageName,
          e);
    }

    return container;
  }
}
