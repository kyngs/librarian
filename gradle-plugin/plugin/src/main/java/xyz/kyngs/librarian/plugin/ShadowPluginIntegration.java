package xyz.kyngs.librarian.plugin;

import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator;
import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class ShadowPluginIntegration {
    protected static List<LibrarianTask.Relocation> extractShadowJarRelocations(Project project) {
        var task = project.getTasks().withType(ShadowJar.class).named("shadowJar").get();

        var relocations = new ArrayList<LibrarianTask.Relocation>();

        for (Relocator relocator : task.getRelocators()) {
            if (relocator instanceof SimpleRelocator simpleRelocator) {
                relocations.add(new LibrarianTask.Relocation(simpleRelocator.getPattern(), simpleRelocator.getShadedPattern()));
            }
        }

        return relocations;
    }
}
