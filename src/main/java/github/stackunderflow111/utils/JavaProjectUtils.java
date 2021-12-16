package github.stackunderflow111.utils;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class JavaProjectUtils {
    private final static Logger logger = LoggerFactory.getLogger(JavaProjectUtils.class);

    public static boolean isJavaProject(Project project) {
        return project.getPluginManager().hasPlugin("java");
    }

    public static Set<URL> getClassPathsFromJavaProject(Project project) {
        Set<URL> classPaths = new HashSet<>();
        JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        for (SourceSet sourceSet : javaExtension.getSourceSets()) {
            FileCollection classesDirs = sourceSet.getOutput().getClassesDirs();
            for (File directory : classesDirs.getFiles()) {
                URL classesUrl = CommonUtils.fileToURL(directory);
                logger.debug("Adding directory '{}' to Classpath", classesUrl);
                classPaths.add(classesUrl);
            }
            File resourcesDir = sourceSet.getOutput().getResourcesDir();
            if (resourcesDir != null) {
                URL resourcesUrl = CommonUtils.fileToURL(resourcesDir);
                logger.debug("Adding directory '{}' to Classpath", resourcesUrl);
                classPaths.add(resourcesUrl);
            }
        }
        return classPaths;
    }
}
