package github.stackunderflow111.steps;

import github.stackunderflow111.CustomAction;
import github.stackunderflow111.extenstion.CustomActionConfig;
import org.gradle.api.Task;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class CustomActionStep implements Step {
    private final CustomActionConfig customActionConfig;

    private final Task task;

    public CustomActionStep(CustomActionConfig customActionConfig, Task task) {
        this.task = task;
        this.customActionConfig = customActionConfig;
    }

    @Override
    public void execute(JdbcDatabaseContainer<?> container) {
        CustomAction<? super Task,? super JdbcDatabaseContainer<?>> customAction = customActionConfig.getCustomAction();
        customAction.execute(task, container);
    }
}
