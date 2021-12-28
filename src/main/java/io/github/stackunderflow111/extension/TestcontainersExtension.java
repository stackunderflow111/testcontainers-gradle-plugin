package io.github.stackunderflow111.extension;

import io.github.stackunderflow111.extension.config.Config;
import io.github.stackunderflow111.extension.config.CustomActionConfig;
import io.github.stackunderflow111.extension.config.FlywayMigrateConfig;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskProvider;
import org.testcontainers.containers.JdbcDatabaseContainer;

public abstract class TestcontainersExtension {
  /** The task to configure. The plugin configures its steps in the task's doFirst block */
  private TaskProvider<? extends Task> task;
  /** The action to run on a container object after it's created (but not yet started) */
  private Action<? super JdbcDatabaseContainer<?>> configureContainerAction = container -> {};

  private final List<Config> configs = new ArrayList<>();

  /** The docker image name, such as "postgres:13" */
  @Input
  public abstract Property<String> getImageName();

  /** The container class name, such as "org.testcontainers.containers.PostgreSQLContainer" */
  @Input
  public abstract Property<String> getContainerClass();

  public TaskProvider<? extends Task> getTask() {
    return task;
  }

  public void setTask(TaskProvider<? extends Task> task) {
    this.task = task;
  }

  public Action<? super JdbcDatabaseContainer<?>> getConfigureContainerAction() {
    return configureContainerAction;
  }

  public void configureContainer(
      Action<? super JdbcDatabaseContainer<?>> configureContainerAction) {
    this.configureContainerAction = configureContainerAction;
  }

  public List<Config> getConfigs() {
    return configs;
  }

  /** Add a flyway migration step to the task's doFirst block. See {@link FlywayMigrateConfig} */
  public void flywayMigrateStep(Action<? super FlywayMigrateConfig> flywayMigrateConfigAction) {
    FlywayMigrateConfig flywayMigrateConfig = new FlywayMigrateConfig();
    flywayMigrateConfigAction.execute(flywayMigrateConfig);
    configs.add(flywayMigrateConfig);
  }

  /** Add a custom step to the task's doFirst block. See {@link CustomActionConfig} */
  public void customStep(Action<? super CustomActionConfig> customActionConfigAction) {
    CustomActionConfig customActionConfig = new CustomActionConfig();
    customActionConfigAction.execute(customActionConfig);
    configs.add(customActionConfig);
  }
}
