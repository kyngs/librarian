plugins {
    id("xyz.kyngs.librarian.java-conventions")
}

dependencies {
    api("com.grack:nanojson:1.7")

    compileOnly("org.apache.maven.resolver:maven-resolver-supplier:1.9.15")
    compileOnly("org.apache.maven:maven-resolver-provider:3.9.4")
}

//blossom {
//    replaceToken("@VERSION@", project.version.toString())
//    replaceToken("@HTTP_USER_AGENT@", "librarian/" + project.version.toString())
//}

description = "librarian-core"
