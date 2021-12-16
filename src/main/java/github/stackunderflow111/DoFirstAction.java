package github.stackunderflow111;

import github.stackunderflow111.extenstion.Config;
import github.stackunderflow111.extenstion.TestcontainersExtension;
import github.stackunderflow111.steps.Step;
import github.stackunderflow111.utils.ConfigurationUtils;
import github.stackunderflow111.utils.JavaProjectUtils;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * The action to run in the doFirst block when the configured task starts.
 *
 * <p>It starts a docker container, and then runs a list of {@link Step}s with this container.
 */
public class DoFirstAction implements Action<Task> {
  private final TestcontainersExtension extension;
  private static final Logger logger = LoggerFactory.getLogger(DoFirstAction.class);

  public DoFirstAction(TestcontainersExtension extension) {
    this.extension = extension;
  }

  /**
   * execute the action on the given task
   *
   * @param task the task to run on
   */
  @Override
  public void execute(@NotNull Task task) {
    // configure steps to run on this container
    List<Step> postStartSteps =
        extension.getConfigs().stream().map(Config::createStep).collect(Collectors.toList());
    logger.info(
        "Steps to execute: {}",
        postStartSteps.stream()
            .map(step -> step.getClass().getSimpleName())
            .collect(Collectors.toSet()));

    // initialize the classloader
    Project project = task.getProject();
    ClassLoader classLoader = initClassLoader(project);
    // starts the docker container
    JdbcDatabaseContainer<?> container = startContainer(extension, project, classLoader);
    ExecutionContext executionContext = new ExecutionContext(container, task, classLoader);

    // run steps
    for (Step step : postStartSteps) {
      logger.info("Executing step: {}", step.getClass().getSimpleName());
      step.execute(executionContext);
    }
    logger.info("Steps executed successfully!");
  }

  private static ClassLoader initClassLoader(Project project) {
    Set<URL> classPaths = new HashSet<>();
    if (JavaProjectUtils.isJavaProject(project)) {
      classPaths.addAll(JavaProjectUtils.getClassPathsFromJavaProject(project));
    }
    classPaths.addAll(ConfigurationUtils.getClassPathsFromConfiguration(project));
    logger.info("ClassPaths for testcontainers: {}", classPaths);

    return new URLClassLoader(
        classPaths.toArray(new URL[0]), project.getBuildscript().getClassLoader());
  }

  private JdbcDatabaseContainer<?> startContainer(
      TestcontainersExtension extension, Project project, ClassLoader classLoader) {
    Action<? super JdbcDatabaseContainer<?>> configureContainerAction =
        extension.getConfigureContainerAction();
    String imageName = extension.getImageName().get();
    String containerClass = extension.getContainerClass().get();
    JdbcDatabaseContainer<?> container =
        startContainer(imageName, containerClass, classLoader, configureContainerAction);
    // register buildFinished hook to stop the container automatically
    project
        .getGradle()
        .buildFinished(
            buildResult -> {
              if (!container.isShouldBeReused()) {
                container.stop();
              }
            });
    return container;
  }

  private JdbcDatabaseContainer<?> startContainer(
      String imageName,
      String containerClass,
      ClassLoader classLoader,
      Action<? super JdbcDatabaseContainer<?>> configureContainerAction) {
    logger.info("Starting a container with image '{}'", imageName);
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
    JdbcDatabaseContainer<?> container;
    try {
      Constructor<JdbcDatabaseContainer<?>> constructor =
          clazz.getConstructor(DockerImageName.class);
      container = constructor.newInstance(DockerImageName.parse(imageName));
    } catch (Exception e) {
      throw new InvalidUserDataException(
          "Could not create a new instance of class "
              + clazz.getSimpleName()
              + " with image "
              + imageName,
          e);
    }
    configureContainerAction.execute(container);
    container.start();
    logger.info("Container started successfully with URL '{}'", container.getJdbcUrl());
    return container;
  }
}
