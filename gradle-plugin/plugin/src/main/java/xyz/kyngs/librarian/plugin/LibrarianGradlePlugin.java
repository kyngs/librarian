package xyz.kyngs.librarian.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * Registers the {@code librarian} task, which generates the librarian metadata file from the
 * dependencies declared in the {@code librarian} configuration.
 */
public class LibrarianGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);

        project.getExtensions().create("librarian", LibrarianExtension.class);

        Configuration customScope = project.getConfigurations().create("librarian");
        project.getConfigurations().getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).extendsFrom(customScope);

        var outputDir = project.getLayout().getBuildDirectory().dir("librarian");

        // Wire the generated metadata into the main resources at configuration time, so that
        // processResources sees it as a regular resource source directory.
        project.getExtensions().getByType(JavaPluginExtension.class)
                .getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .getResources()
                .srcDir(outputDir);

        project.getTasks().register("librarian", LibrarianTask.class, customScope, project, outputDir);
        project.getTasks().withType(ProcessResources.class).configureEach(task -> task.dependsOn("librarian"));
    }
}
