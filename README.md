# Libby

A runtime dependency management library for Java projects, primarily designed for Java-based Minecraft server plugins.

Libraries can be downloaded from Maven repositories (or direct URLs) into a plugin's data
folder, relocated and then loaded into the plugin's classpath at runtime.

Or you can use the automatic Gradle integration with the [Gradle plugin](#gradle-plugin).

### Why use runtime dependency management?

Hosting services like SpigotMC limit plugin file size, and bundling dependencies can push a
plugin over that limit. With runtime dependency management, dependencies are downloaded and
cached by the server instead of being bundled, keeping the plugin jar small. That also means
faster downloads and less bandwidth strain when self-hosting.

### Maven Central and other public repositories note

Libby downloads dependencies from remote repositories at runtime, on every server that runs your
plugin. Pointing it at Maven Central (or other public repositories such as Sonatype) effectively
uses them as a CDN to serve dependencies to end users, which their infrastructure is not meant for.

Host your own mirror of the repositories you depend on and configure Libby to use it instead. This
keeps traffic on infrastructure you control and avoids upstream availability or rate-limiting issues.

### Usage

Add the repository and dependency (Gradle example):
```kts
maven { url = uri("https://repo.kyngs.xyz/public/") }

implementation("xyz.kyngs.libby:libby-paper:2.0.0-SNAPSHOT") // replace paper with your platform
```

**Always** relocate Libby to avoid conflicts:
```kts
relocate("xyz.kyngs.libby", "your.package.lib.libby")
```

Create a LibraryManager for your platform:
```java
PaperLibraryManager libraryManager = new PaperLibraryManager(plugin);
```

Build a Library:
```java
Library lib = Library.builder()
    .groupId("your{}dependency{}groupId") // "{}" becomes ".", avoiding relocation by shade
    .artifactId("artifactId")
    .version("version")
    // the rest are optional:
    .id("my-lib") // libraries sharing an id load into a common IsolatedClassLoader
    .relocate("package{}to{}relocate", "the{}relocated{}package")
    .isolatedLoad(true)
    .classifier("customClassifier")
    .checksum("Base64-encoded SHA-256 checksum")
    .build();
```

Add a repository, then download and load the library. `loadLibrary` handles both:
```java
libraryManager.addMavenCentral();
libraryManager.loadLibrary(lib);
```

## Gradle plugin

The Gradle plugin lets you declare Libby dependencies in your build script instead of specifying
them manually in code. On build, it generates a `libby.json` inside your JAR listing all
dependencies (including transitive ones) and their repositories, which Libby loads at runtime.

### Adding the plugin

Add the plugin repository in `settings.gradle`:

```groovy
pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.kyngs.xyz/gradle-plugins")
        }
        gradlePluginPortal()
    }
}
```

Then apply it in `build.gradle`:

```groovy
plugins {
    id 'xyz.kyngs.libby.plugin' version '1.2.1'
}
```

### Declaring dependencies

Replace the `compileOnly` configuration with `libby`:

```groovy
dependencies {
    libby 'com.zaxxer:HikariCP:5.0.1'
}
```

The libby task, run automatically on build, writes the `libby.json` described above into the final JAR.

### Linking with Libby

The plugin only generates `libby.json`; you still have to load it. Call
`LibraryManager.configureFromJSON()` to do so.

### Further configuration

#### Relocating

Relocation is important when bundling libraries. Add the `shadow` plugin alongside Libby:

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
    id 'xyz.kyngs.libby.plugin' version '1.2.1'
}
```

Then define the relocation rules:

```groovy
shadowJar {
    relocate 'com.zaxxer.hikari', 'com.example.hikari'
}
```

See the [shadow plugin documentation](https://imperceptiblethoughts.com/shadow/configuration/relocation/) for details.

**If you use relocation, you must build with the shadowJar task.**

#### Excluding dependencies

The plugin resolves all transitive dependencies, some of which may be unnecessary. For example,
`com.zaxxer:HikariCP` pulls in `org.slf4j:slf4j-api`, which platforms like Bukkit already bundle,
so downloading it is redundant and can cause conflicts.

Exclude a dependency with a regular expression matched against each dependency id
(`groupId:artifactId:version`):

```groovy
libby {
    excludeDependency 'org.slf4j:.*:.*'
}
```

The example above excludes everything in the `org.slf4j` group.

## Credits

Special thanks to:

* [AlessioDP](https://github.com/AlessioDP/libby) and [Byteflux](https://github.com/Byteflux/libby) for creating the base of this library
