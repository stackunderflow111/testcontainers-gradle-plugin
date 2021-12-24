package io.github.stackunderflow111;

import io.github.stackunderflow111.extenstion.TestcontainersExtension;
import io.github.stackunderflow111.utils.ConfigurationUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class TestcontainersPlugin implements Plugin<Project> {
  @Override
  public void apply(@NotNull Project project) {
    TestcontainersExtension testcontainersExtension =
        project.getExtensions().create("testcontainers", TestcontainersExtension.class);
    ConfigurationUtils.createRuntimeConfiguration(project);
    ConfigureTask configureTask = new ConfigureTask(testcontainersExtension);
    project.afterEvaluate(configureTask);
  }
}
