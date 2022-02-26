package io.github.stackunderflow111.plugins.testcontainers;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.services.BuildServiceRegistry;

public class TestcontainersPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TestcontainersExtension testcontainersExtension =
                project.getExtensions().create("testcontainers", TestcontainersExtension.class);
        BuildServiceRegistry buildServiceRegistry = project.getGradle().getSharedServices();
        testcontainersExtension
                .getDatabaseContainerBuildServices()
                .configureEach(
                        config ->
                                buildServiceRegistry.registerIfAbsent(
                                        config.getName(),
                                        DatabaseContainer.class,
                                        spec -> {
                                            DatabaseContainer.Params params = spec.getParameters();
                                            params.getImageName().set(config.getImageName());
                                            params.getContainerClass()
                                                    .set(config.getContainerClass());
                                        }));
    }
}
