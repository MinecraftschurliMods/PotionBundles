plugins {
    idea
    id ("com.github.minecraftschurlimods.helperplugin")
}

helper.withTestSourceSet()

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
}

helper.withCommonRuns()
helper.withGameTestRuns()
helper.modproperties.put(
    "catalogueItemIcon", helper.projectId.map { "$it:potion_bundle{Potion:\"minecraft:water\"}" }
)
helper.dependency(
    "jei",
    project.properties["jei_version_range"] as String,
    "optional"
)

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
            organization = "Minecraftschurli Mods"
            organizationUrl = "https://github.com/Minecraftschurli"
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
