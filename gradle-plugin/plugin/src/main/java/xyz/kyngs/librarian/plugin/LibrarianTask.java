package xyz.kyngs.librarian.plugin;

import com.grack.nanojson.JsonWriter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.inject.Inject;

@DisableCachingByDefault(because = "Generates librarian metadata from the resolved dependency graph at execution time")
public class LibrarianTask extends DefaultTask {

    private final Configuration customScope;
    private final Project project;
    private final Provider<Directory> outputDir;

    @Inject
    public LibrarianTask(Configuration customScope, Project project, Provider<Directory> outputDir) {
        this.customScope = customScope;
        this.project = project;
        this.outputDir = outputDir;
    }

    @TaskAction
    public void run() throws NoSuchAlgorithmException {
        var extension = project.getExtensions().getByType(LibrarianExtension.class);
        var excludedDependencies = extension.getExcludedDependencies();
        var noChecksumDependencies = extension.getNoChecksumDependencies();

        var output = outputDir.get().file("librarian.json").getAsFile();
        output.getParentFile().mkdirs();

        var writer = JsonWriter.string();

        writer.object();
        writer.value("version", 0);

        writer.array("libraries");

        var md = MessageDigest.getInstance("SHA-256");

        for (ResolvedArtifact artifact : customScope.getResolvedConfiguration().getResolvedArtifacts()) {
            var id = artifact.getModuleVersion().getId();

            if (excludedDependencies.stream().anyMatch(id.toString()::matches)) continue;

            writer.object();
            writer.value("group", id.getGroup().replace(".", "{}"));
            writer.value("name", id.getName());
            writer.value("version", id.getVersion());
            if (artifact.getClassifier() != null) writer.value("classifier", artifact.getClassifier());
            if (!artifact.getType().equals("jar") || noChecksumDependencies.stream().anyMatch(id.toString()::matches)) {
                writer.end();
                continue;
            }
            try {
                var hash = md.digest(Files.readAllBytes(artifact.getFile().toPath()));
                writer.value("checksum", Base64.getEncoder().encodeToString(hash));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            writer.end();
        }

        // End libraries array
        writer.end();

        writer.array("repositories");

        for (var repository : project.getRepositories()) {
            if (repository instanceof MavenArtifactRepository maven) {
                var path = maven.getUrl().toString();
                if (!path.startsWith("http")) continue;
                writer.value(path);
            }
        }

        // End repositories array
        writer.end();

        var relocations = extractShadowJarRelocations();

        if (relocations != null) {
            writer.object("relocations");
            for (var relocation : relocations) {
                writer.value(relocation.from.replace(".", "{}"), relocation.to.replace(".", "{}"));
            }
            // End relocations object
            writer.end();
        }

        writer.end();
        try (FileWriter fileWriter = new FileWriter(output)) {
            fileWriter.write(writer.done());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write custom scope dependencies to JSON file", e);
        }
    }

    private List<Relocation> extractShadowJarRelocations() {
        if (project.getTasks().findByName("shadowJar") == null) return null;
        return ShadowPluginIntegration.extractShadowJarRelocations(project); //Move to a separate class to avoid class loading issues
    }

    protected record Relocation(String from, String to) {
    }
}
