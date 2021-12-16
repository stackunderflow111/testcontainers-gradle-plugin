package github.stackunderflow111.extenstion;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.gradle.api.Action;

public class FlywayMigrateConfig implements Config {
    private Action<? super FluentConfiguration> flywayConfigurationAction = config -> {};

    public Action<? super FluentConfiguration> getFlywayConfigurationAction() {
        return flywayConfigurationAction;
    }

    /**
     * Configures the flyway configuration before migration runs
     */
    public void configureFlyway(Action<? super FluentConfiguration> flywayConfigurationAction) {
        this.flywayConfigurationAction = flywayConfigurationAction;
    }
}
