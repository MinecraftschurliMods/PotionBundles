pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "1.0.15"
        id("com.github.minecraftschurlimods.helperplugin") version "2.0"
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url = uri("https://maven.neoforged.net/releases") }
        maven { url = uri("https://minecraftschurli.ddns.net/repository/maven-public") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "PotionBundles"
