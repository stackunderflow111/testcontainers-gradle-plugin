package github.stackunderflow111.extenstion;

import github.stackunderflow111.CustomAction;
import org.gradle.api.Task;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class CustomActionConfig implements Config {
    private CustomAction<? super Task,? super JdbcDatabaseContainer<?>> customAction = (task, container) -> {};

    public CustomAction<? super Task,? super JdbcDatabaseContainer<?>> getCustomAction() {
        return customAction;
    }

    /**
     * Run customAction for this step
     * @param customAction the action to run. It takes two arguments,
     *                     the first one is the configured task, and the second one is the started container
     */
    public void run(CustomAction<? super Task,? super JdbcDatabaseContainer<?>> customAction) {
        this.customAction = customAction;
    }
}
