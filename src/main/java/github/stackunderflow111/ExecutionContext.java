package github.stackunderflow111;

import org.gradle.api.Task;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class ExecutionContext {
  private final JdbcDatabaseContainer<?> container;
  private final Task task;
  private final ClassLoader classLoader;

  public ExecutionContext(JdbcDatabaseContainer<?> container, Task task, ClassLoader classLoader) {
    this.container = container;
    this.task = task;
    this.classLoader = classLoader;
  }

  public JdbcDatabaseContainer<?> getContainer() {
    return container;
  }

  public Task getTask() {
    return task;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }
}
