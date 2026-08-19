tasks.register("build") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:build"))
}

tasks.register("publish") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publish"))
}
