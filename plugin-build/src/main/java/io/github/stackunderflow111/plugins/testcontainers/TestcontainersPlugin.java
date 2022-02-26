package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TestcontainersPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TestcontainersExtension testcontainersExtension =
                project.getExtensions().create("testcontainers", TestcontainersExtension.class);
        project.getGradle()
                .getSharedServices()
                .registerIfAbsent(
                        "testcontainers",
                        DatabaseContainer.class,
                        spec -> {
                            // Provide some parameters
                            DatabaseContainer.Params params = spec.getParameters();
                            params.getImageName().set(testcontainersExtension.getImageName());
                            params.getContainerClass()
                                    .set(testcontainersExtension.getContainerClass());
                        });
    }
}
