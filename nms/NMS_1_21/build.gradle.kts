plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    compileOnly(project(":Images-Common"))
}

configurations.reobf {
    outgoing.artifact(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
}