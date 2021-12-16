package github.stackunderflow111;

import github.stackunderflow111.extenstion.Config;
import github.stackunderflow111.extenstion.TestcontainersExtension;
import github.stackunderflow111.steps.Step;
import github.stackunderflow111.utils.ConfigurationUtils;
import github.stackunderflow111.utils.ContainerUtils;
import github.stackunderflow111.utils.JavaProjectUtils;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

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
    Project project = task.getProject();
    ClassLoader classLoader = initClassLoader(project);
    JdbcDatabaseContainer<?> container = startContainer(extension, project, classLoader);
    ExecutionContext executionContext = new ExecutionContext(container, task, classLoader);
    List<Step> postStartSteps = configurePostStartSteps(extension.getConfigs());
    // run steps
    for (Step step : postStartSteps) {
      logger.info("Executing step: {}", step.getClass().getSimpleName());
      step.execute(executionContext);
    }
    logger.info("Steps executed successfully!");
  }

  private static List<Step> configurePostStartSteps(List<Config> configs) {
    List<Step> postStartSteps =
        configs.stream().map(Config::createStep).collect(Collectors.toList());
    logger.info(
        "Steps to execute: {}",
        postStartSteps.stream()
            .map(step -> step.getClass().getSimpleName())
            .collect(Collectors.toSet()));
    return postStartSteps;
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

  private static JdbcDatabaseContainer<?> startContainer(
      TestcontainersExtension extension, Project project, ClassLoader classLoader) {
    Action<? super JdbcDatabaseContainer<?>> configureContainerAction =
        extension.getConfigureContainerAction();
    String imageName = extension.getImageName().get();
    String containerClass = extension.getContainerClass().get();
    JdbcDatabaseContainer<?> container =
        ContainerUtils.initContainer(imageName, containerClass, classLoader);
    configureContainerAction.execute(container);
    logger.info("Starting a container with image '{}'", imageName);
    container.start();
    logger.info("Container started successfully with URL '{}'", container.getJdbcUrl());
    // register buildFinished hook to stop the container automatically
    Runnable containerStopHook = ContainerUtils.getContainerStopHook(container);
    project.getGradle().buildFinished(buildResult -> containerStopHook.run());
    return container;
  }
}
