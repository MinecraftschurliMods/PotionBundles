import com.github.minecraftschurlimods.helperplugin.version

plugins {
    idea
    id("net.neoforged.moddev")
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

val jei = helper.dependencies.jei()

dependencies {
    val jeiApiDep = helper.minecraftVersion.zip(jei.version) { mc, version -> "mezz.jei:jei-${mc}-common-api:${version}" }
    val jeiDep = helper.minecraftVersion.zip(jei.version) { mc, version -> "mezz.jei:jei-${mc}-neoforge:${version}" }
    compileOnly(jeiApiDep)
    runtimeOnly(jeiDep)
    implementation("org.jetbrains:annotations:23.0.0")
    "testCompileOnly"("org.jetbrains:annotations:23.0.0")
}

helper.withCommonRuns()
helper.withGameTestRuns()
helper.modproperties.put(
    "catalogueItemIcon", helper.projectId.map { "$it:potion_bundle[minecraft:potion_contents=\"minecraft:water\"]" }
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
