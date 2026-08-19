rootProject.name = "librarian"
include(":librarian-paper")
include(":librarian-core")
include(":librarian-velocity")
project(":librarian-paper").projectDir = file("paper")
project(":librarian-core").projectDir = file("core")
project(":librarian-velocity").projectDir = file("velocity")

includeBuild("gradle-plugin")
