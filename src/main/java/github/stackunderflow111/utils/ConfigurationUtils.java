package github.stackunderflow111.utils;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationUtils {
    private final static String CONFIGURATION_NAME = "testcontainersRuntime";
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

    public static void createRuntimeConfiguration(Project project) {
        Configuration runtimeConfiguration = project.getConfigurations().create(CONFIGURATION_NAME);
        runtimeConfiguration.setDescription("The classpath used to prepare the jOOQ code generator.");
    }

    public static Set<URL> getClassPathsFromConfiguration(Project project) {
        logger.debug("Adding artifacts in configuration '{}' to classpath", CONFIGURATION_NAME);
        Set<URL> classPaths = new HashSet<>();
        ResolvedConfiguration resolvedConfiguration =
                project.getConfigurations().getByName(CONFIGURATION_NAME).getResolvedConfiguration();
        for (ResolvedArtifact artifact : resolvedConfiguration.getResolvedArtifacts()) {
            URL artifactUrl = CommonUtils.fileToURL(artifact.getFile());
            logger.debug("Adding artifact '{}' to classpath: ", artifactUrl);
            classPaths.add(artifactUrl);
        }
        return classPaths;
    }

}
