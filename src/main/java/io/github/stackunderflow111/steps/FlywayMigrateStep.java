package io.github.stackunderflow111.steps;

import io.github.stackunderflow111.ExecutionContext;
import io.github.stackunderflow111.extension.config.FlywayMigrateConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.gradle.api.Action;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class FlywayMigrateStep implements Step {
  private final FlywayMigrateConfig flywayMigrateConfig;

  public FlywayMigrateStep(FlywayMigrateConfig flywayMigrateConfig) {
    this.flywayMigrateConfig = flywayMigrateConfig;
  }

  @Override
  public void execute(ExecutionContext executionContext) {
    JdbcDatabaseContainer<?> container = executionContext.getContainer();
    ClassLoader classLoader = executionContext.getClassLoader();
    FluentConfiguration configuration =
        Flyway.configure(classLoader)
            .dataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    Action<? super FluentConfiguration> flywayConfigurationAction =
        flywayMigrateConfig.getFlywayConfigurationAction();
    flywayConfigurationAction.execute(configuration);
    Flyway flyway = configuration.load();
    flyway.migrate();
  }
}
