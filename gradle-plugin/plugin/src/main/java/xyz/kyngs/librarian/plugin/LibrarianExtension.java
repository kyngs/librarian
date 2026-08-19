package xyz.kyngs.librarian.plugin;

import java.util.ArrayList;
import java.util.List;

public class LibrarianExtension {
    private List<String> excludedDependencies = new ArrayList<>();
    private List<String> noChecksumDependencies = new ArrayList<>();

    public List<String> getExcludedDependencies() {
        return excludedDependencies;
    }

    public List<String> getNoChecksumDependencies() {
        return noChecksumDependencies;
    }

    /**
     * Add a dependency to exclude from the librarian.json file. <br>
     * <br>
     * The dependency is a regex matching the format "group:name:version" <br>
     * For example "org\\.company:library:.*" will exclude all versions of the library "library" from the group "org.company"
     *
     * @param dependency The dependency to exclude
     */
    public void excludeDependency(String dependency) {
        excludedDependencies.add(dependency);
    }

    /**
     * Add a dependency to exclude from the checksum calculation. <br>
     * <br>
     * The dependency is a regex matching the format "group:name:version" <br>
     * For example "org\\.company:library:.*" will exclude all versions of the library "library" from the group "org.company"
     */
    public void noChecksumDependency(String dependency) {
        noChecksumDependencies.add(dependency);
    }
}
