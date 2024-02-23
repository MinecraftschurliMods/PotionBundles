plugins {
    idea
    id ("com.github.minecraftschurlimods.helperplugin")
}

repositories {
    maven {
        name = "blamejared Maven"
        url = uri("https://maven.blamejared.com")
    }
    maven {
        name = "ModMaven"
        url = uri("https://modmaven.k-4u.nl")
    }
}

dependencies {
    implementation(helper.neoforge())
    compileOnly("mezz.jei:jei-1.20.4-common-api:${project.properties["jei_version"]}")
    runtimeOnly("mezz.jei:jei-1.20.4-neoforge:${project.properties["jei_version"]}")
    implementation("org.jetbrains:annotations:23.0.0")
    "testCompileOnly"("org.jetbrains:annotations:23.0.0")
}

helper.withTestSourceSet()
helper.withCommonRuns()
helper.withGameTestRuns()
helper.modproperties.put(
    "catalogueItemIcon", helper.projectId.map { "$it:potion_bundle{Potion:\"minecraft:water\"}" }
)
helper.dependencies.jei()

helper.publication.pom {
    organization {
        name = "Minecraftschurli Mods"
        url = "https://github.com/MinecraftschurliMods"
    }
    developers {
        developer {
            id = "minecraftschurli"
            name = "Minecraftschurli"
            email = "minecraftschurli@gmail.com"
            url = "https://github.com/Minecraftschurli"
            organization = "Minecraftschurli Mods"
            organizationUrl = "https://github.com/MinecraftschurliMods"
            timezone = "Europe/Vienna"
        }
        developer {
            id = "ichhabehunger54"
            name = "IchHabeHunger54"
            url = "https://github.com/IchHabeHunger54"
            organization = "Minecraftschurli Mods"
            organizationUrl = "https://github.com/MinecraftschurliMods"
            timezone = "Europe/Vienna"
        }
    }
}
