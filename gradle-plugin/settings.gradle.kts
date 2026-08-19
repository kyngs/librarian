rootProject.name = "librarian-gradle-plugin"
include("plugin")

val rootProperties = java.util.Properties()
settingsDir.resolve("../gradle.properties").inputStream().use(rootProperties::load)
gradle.allprojects {
    version = rootProperties.getProperty("version")
}
