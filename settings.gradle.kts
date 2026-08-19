rootProject.name = "libby"
include(":libby-paper")
include(":libby-core")
include(":libby-velocity")
project(":libby-paper").projectDir = file("paper")
project(":libby-core").projectDir = file("core")
project(":libby-velocity").projectDir = file("velocity")

includeBuild("gradle-plugin")
