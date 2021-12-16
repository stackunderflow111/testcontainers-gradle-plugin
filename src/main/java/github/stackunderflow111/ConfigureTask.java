package github.stackunderflow111;

import github.stackunderflow111.extenstion.TestcontainersExtension;
import github.stackunderflow111.utils.ConfigurationUtils;
import github.stackunderflow111.utils.JavaProjectUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * Configure the task defined in the "testcontainers" extension to run {@link DoFirstAction} in its doFirst block
 */
public class ConfigureTask implements Action<Project> {
    private final TestcontainersExtension extension;

    private final static Logger logger = LoggerFactory.getLogger(ConfigureTask.class);

    public ConfigureTask(TestcontainersExtension extension) {
        this.extension = extension;
    }

    @Override
    public void execute(@NotNull Project project) {
        TaskProvider<? extends Task> task = extension.getTask();
        if (task == null) {
            logger.warn("the property 'task' is missing in configuration block 'testcontainers', " +
                    "this plugin will not be active");
        } else {
            ClassLoader classLoader = initClassLoader(project);
            DoFirstAction doFirstAction = new DoFirstAction(extension, classLoader);
            task.configure(task1 -> task1.doFirst(doFirstAction));
        }
    }

    private static ClassLoader initClassLoader(Project project) {
        Set<URL> classPaths = new HashSet<>();
        if (JavaProjectUtils.isJavaProject(project)) {
            classPaths.addAll(JavaProjectUtils.getClassPathsFromJavaProject(project));
        }
        classPaths.addAll(ConfigurationUtils.getClassPathsFromConfiguration(project));
        logger.info("ClassPaths for testcontainers: {}", classPaths);

        return new URLClassLoader(
                classPaths.toArray(new URL[0]),
                project.getBuildscript().getClassLoader());
    }
}
