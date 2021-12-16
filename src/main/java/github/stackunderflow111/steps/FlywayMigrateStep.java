package github.stackunderflow111.steps;

import github.stackunderflow111.extenstion.FlywayMigrateConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.gradle.api.Action;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class FlywayMigrateStep implements Step {
    private final FlywayMigrateConfig flywayMigrateConfig;
    private final ClassLoader classLoader;

    public FlywayMigrateStep(FlywayMigrateConfig flywayMigrateConfig, ClassLoader classLoader) {
        this.flywayMigrateConfig = flywayMigrateConfig;
        this.classLoader = classLoader;
    }

    @Override
    public void execute(JdbcDatabaseContainer<?> container) {
        FluentConfiguration configuration = Flyway.configure(classLoader)
                .dataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        Action<? super FluentConfiguration> flywayConfigurationAction =
                flywayMigrateConfig.getFlywayConfigurationAction();
        flywayConfigurationAction.execute(configuration);
        Flyway flyway = configuration.load();
        flyway.migrate();
    }
}
