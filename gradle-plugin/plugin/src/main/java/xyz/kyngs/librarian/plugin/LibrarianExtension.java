package xyz.kyngs.librarian.plugin;

import java.util.ArrayList;
import java.util.List;

public class LibrarianExtension {
    private final List<String> excludedDependencies = new ArrayList<>();
    private final List<String> noChecksumDependencies = new ArrayList<>();
    private String mavenCentralRepositoryUrl = "https://maven-central.storage-download.googleapis.com/maven2";

    public List<String> getExcludedDependencies() {
        return excludedDependencies;
    }

    public List<String> getNoChecksumDependencies() {
        return noChecksumDependencies;
    }

    public String getMavenCentralRepositoryUrl() {
        return mavenCentralRepositoryUrl;
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

    /**
     * Set the Maven Central repository URL used for downloading dependencies. <br>
     * By default, it is set to "<a href="https://maven-central.storage-download.googleapis.com/maven2">...</a>". <br>
     * @param mavenCentralRepositoryUrl The Maven Central repository URL to set, or null to use the actual Maven Central URL
     */
    public void setMavenCentralRepositoryUrl(String mavenCentralRepositoryUrl) {
        this.mavenCentralRepositoryUrl = mavenCentralRepositoryUrl;
    }
}
