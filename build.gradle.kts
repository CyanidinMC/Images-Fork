plugins {
    id("java-library")
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.andavin"
version = "2.3.2"

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

    tasks.test {
        useJUnitPlatform()
    }
}


