package xyz.kyngs.librarian.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * A simple 'hello world' plugin.
 */
public class LibrarianGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create("librarian", LibrarianExtension.class);

        Configuration customScope = project.getConfigurations().create("librarian");

        project.getPlugins().apply(JavaPlugin.class);
        project.getConfigurations().getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).extendsFrom(customScope);

        project.getTasks().register("librarian", LibrarianTask.class, customScope, project);
        project.getTasks().withType(ProcessResources.class).configureEach(task -> {
            task.dependsOn("librarian");
        });
    }
}
