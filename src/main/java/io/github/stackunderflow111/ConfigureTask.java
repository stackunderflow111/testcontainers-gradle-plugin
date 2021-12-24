package io.github.stackunderflow111;

import io.github.stackunderflow111.extenstion.TestcontainersExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure the task defined in the "testcontainers" extension to run {@link DoFirstAction} in its
 * doFirst block
 */
public class ConfigureTask implements Action<Project> {
  private final TestcontainersExtension extension;

  private static final Logger logger = LoggerFactory.getLogger(ConfigureTask.class);

  public ConfigureTask(TestcontainersExtension extension) {
    this.extension = extension;
  }

  @Override
  public void execute(@NotNull Project project) {
    TaskProvider<? extends Task> task = extension.getTask();
    if (task == null) {
      logger.warn(
          "the property 'task' is missing in configuration block 'testcontainers', "
              + "this plugin will not be active");
    } else {
      DoFirstAction doFirstAction = new DoFirstAction(extension);
      task.configure(task1 -> task1.doFirst(doFirstAction));
    }
  }
}
