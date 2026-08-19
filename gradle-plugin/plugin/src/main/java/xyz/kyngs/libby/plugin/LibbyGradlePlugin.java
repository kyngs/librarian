package xyz.kyngs.libby.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * A simple 'hello world' plugin.
 */
public class LibbyGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create("libby", LibbyExtension.class);

        Configuration customScope = project.getConfigurations().create("libby");

        project.getPlugins().apply(JavaPlugin.class);
        project.getConfigurations().getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).extendsFrom(customScope);

        project.getTasks().register("libby", LibbyTask.class, customScope, project);
        project.getTasks().withType(ProcessResources.class).configureEach(task -> {
            task.dependsOn("libby");
        });
    }
}
