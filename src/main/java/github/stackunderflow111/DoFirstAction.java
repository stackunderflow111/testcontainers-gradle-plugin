package github.stackunderflow111;

import github.stackunderflow111.extenstion.Config;
import github.stackunderflow111.extenstion.CustomActionConfig;
import github.stackunderflow111.extenstion.FlywayMigrateConfig;
import github.stackunderflow111.extenstion.TestcontainersExtension;
import github.stackunderflow111.steps.FlywayMigrateStep;
import github.stackunderflow111.steps.CustomActionStep;
import github.stackunderflow111.steps.Step;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Task;
import org.slf4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The action to run in the doFirst block when the configured task starts.
 *
 * The action starts a docker container, and then runs a list of {@link Step}s with this container.
 */
public class DoFirstAction implements Action<Task> {
    private final TestcontainersExtension extension;
    private final ClassLoader classLoader;
    private static final Logger logger = LoggerFactory.getLogger(DoFirstAction.class);

    public DoFirstAction(TestcontainersExtension extension, ClassLoader classLoader) {
        this.extension = extension;
        this.classLoader = classLoader;
    }

    /**
     * execute the action on the given task
     * @param task the task to run on
     */
    @Override
    public void execute(@NotNull Task task) {
        // starts the docker container
        Action<? super JdbcDatabaseContainer<?>> configureContainerAction = extension.getConfigureContainerAction();
        String imageName = extension.getImageName().get();
        String containerClass = extension.getContainerClass().get();
        JdbcDatabaseContainer<?> container =
                startContainer(imageName, containerClass, classLoader, configureContainerAction);
        // register buildFinished hook to stop the container automatically
        task.getProject().getGradle().buildFinished(buildResult -> {
            if (!container.isShouldBeReused()) {
                container.stop();
            }
        });
        // configure steps to run on this container
        List<Step> postStartSteps = new ArrayList<>();
        List<Config> configs = extension.getConfigs();
        for (Config config : configs) {
            if (config instanceof FlywayMigrateConfig) {
                FlywayMigrateStep flywayMigrateStep = new FlywayMigrateStep((FlywayMigrateConfig) config, classLoader);
                postStartSteps.add(flywayMigrateStep);
            } else if (config instanceof CustomActionConfig) {
                CustomActionStep customActionStep = new CustomActionStep((CustomActionConfig) config, task);
                postStartSteps.add(customActionStep);
            }
        }
        logger.info("Steps to execute: {}", postStartSteps
                .stream()
                .map(step -> step.getClass().getSimpleName())
                .collect(Collectors.toSet())
        );
        // run steps
        for (Step step : postStartSteps) {
            logger.info("Executing step: {}", step.getClass().getSimpleName());
            step.execute(container);
        }
        logger.info("Steps executed successfully, let's hand over the job to Jooq!");
    }

    private JdbcDatabaseContainer<?> startContainer(String imageName,
                                                    String containerClass,
                                                    ClassLoader classLoader,
                                                    Action<? super JdbcDatabaseContainer<?>> configureContainerAction) {
        logger.info("Starting a container with image name '{}'", imageName);

        Class<JdbcDatabaseContainer<?>> clazz;
        try {
            clazz = (Class<JdbcDatabaseContainer<?>>) Class.forName(containerClass, true, classLoader)
                    .asSubclass(JdbcDatabaseContainer.class);
        } catch (ClassNotFoundException e) {
            throw new InvalidUserDataException("class " + containerClass +
                    " not found, check your 'containerClass' property", e);
        }
        JdbcDatabaseContainer<?> container;
        try {
            Constructor<JdbcDatabaseContainer<?>> constructor = clazz.getConstructor(DockerImageName.class);
            container = constructor.newInstance(DockerImageName.parse(imageName));
        } catch (Exception e) {
            throw new GradleException(
                    "Internal error, could not create a new instance of class " + clazz.getSimpleName(),
                    e
            );
        }
        configureContainerAction.execute(container);
        container.start();
        logger.info("Container started successfully with URL '{}'", container.getJdbcUrl());
        return container;
    }
}
