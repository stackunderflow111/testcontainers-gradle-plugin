package github.stackunderflow111.steps;

import org.testcontainers.containers.JdbcDatabaseContainer;

public interface Step {
    void execute(JdbcDatabaseContainer<?> container);
}
