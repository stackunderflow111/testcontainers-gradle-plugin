package github.stackunderflow111.extenstion;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskProvider;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.ArrayList;
import java.util.List;

abstract public class TestcontainersExtension {
    /**
     * The task to configure. The plugin configures its steps in the task's doFirst block
     */
    private TaskProvider<? extends Task> task;
    /**
     * The action to run on a container object after it's created (but not yet started)
     */
    private Action<? super JdbcDatabaseContainer<?>> configureContainerAction = container -> {};
    private final List<Config> configs = new ArrayList<>();

    /**
     * The docker image name, such as "postgres:13"
     */
    @Input
    abstract public Property<String> getImageName();

    /**
     * The container class name, such as "org.testcontainers.containers.PostgreSQLContainer"
     */
    @Input
    abstract public Property<String> getContainerClass();

    public TaskProvider<? extends Task> getTask() {
        return task;
    }

    public void setTask(TaskProvider<? extends Task> task) {
        this.task = task;
    }

    public Action<? super JdbcDatabaseContainer<?>> getConfigureContainerAction() {
        return configureContainerAction;
    }

    public void configureContainer(Action<? super JdbcDatabaseContainer<?>> configureContainerAction) {
        this.configureContainerAction = configureContainerAction;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    /**
     * Add a flyway migration step to the task's doFirst block. See {@link FlywayMigrateConfig}
     */
    public void flywayMigrateStep(Action<? super FlywayMigrateConfig> flywayMigrateConfigAction) {
        FlywayMigrateConfig flywayMigrateConfig = new FlywayMigrateConfig();
        flywayMigrateConfigAction.execute(flywayMigrateConfig);
        configs.add(flywayMigrateConfig);
    }

    /**
     * Add a custom step to the task's doFirst block. See {@link CustomActionConfig}
     */
    public void customActionStep(Action<? super CustomActionConfig> customActionConfigAction) {
        CustomActionConfig customActionConfig = new CustomActionConfig();
        customActionConfigAction.execute(customActionConfig);
        configs.add(customActionConfig);
    }

}
