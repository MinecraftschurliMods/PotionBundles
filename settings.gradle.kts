pluginManagement {
    plugins {
        `maven-publish`
        eclipse
        idea
        java
        val forgegradle_version: String by settings
        val librarian_version: String by settings
        id("net.minecraftforge.gradle") version(forgegradle_version)
        id("org.parchmentmc.librarian.forgegradle") version(librarian_version)
    }
    repositories {
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "Parchment"
            url = uri("https://maven.parchmentmc.org")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}