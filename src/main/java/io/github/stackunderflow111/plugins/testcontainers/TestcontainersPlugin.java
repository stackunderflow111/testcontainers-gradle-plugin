package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class TestcontainersPlugin implements Plugin<Project> {
  @Override
  public void apply(@NotNull Project project) {
    TestcontainersExtension testcontainersExtension =
        project.getExtensions().create("testcontainers", TestcontainersExtension.class);
    project
        .getGradle()
        .getSharedServices()
        .registerIfAbsent(
            "testcontainers",
            Container.class,
            spec -> {
              // Provide some parameters
              Container.Params params = spec.getParameters();
              params.getImageName().set(testcontainersExtension.getImageName());
              params.getContainerClass().set(testcontainersExtension.getContainerClass());
            });
  }
}
