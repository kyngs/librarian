package xyz.kyngs.librarian;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * A runtime dependency manager for Paper Plugins. (Not to be confused with bukkit plugins loaded on paper)
 * See: <a href="https://docs.papermc.io/paper/dev/getting-started/paper-plugins">Paper docs</a>
 */
public class PaperLibraryManager extends LibraryManager implements PluginLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaperLibraryManager.class);

    private final List<Path> requestedLibraries = new LinkedList<>();

    /**
     * Creates a new Paper library manager.
     */
    public PaperLibraryManager() {
        this(Path.of("libraries"));
    }

    /**
     * Creates a new Paper library manager.
     *
     * @param cacheDirectory the directory where all libraries will be cached
     */
    public PaperLibraryManager(Path cacheDirectory) {
        super(cacheDirectory);
    }

    /**
     * Adds a file to the Paper plugin's library classpath.
     *
     * @param file the file to add
     */
    @Override
    protected void addToClasspath(Path file) {
        requestedLibraries.add(file);
    }

    @Override
    protected InputStream getPluginResourceAsInputStream(String path) throws UnsupportedOperationException {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        if (requestedLibraries.isEmpty()) {
            LOGGER.warn("No libraries were requested to be added to the classpath during plugin loading. Perhaps you forgot to call LibraryManager#loadLibrary before the loader hook?");
            return;
        }

        for (Path path : requestedLibraries) {
            classpathBuilder.addLibrary(new JarLibrary(path));
        }
    }
}
