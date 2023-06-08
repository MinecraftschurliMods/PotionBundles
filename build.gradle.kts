//file:noinspection GroovyAssignabilityCheck
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.util.Node
import groovy.util.NodeList
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

val mod_group: String by project
val mc_version: String by project
val mod_version: String by project
val mod_id: String by project
val forge_version: String by project
val jei_version: String by project
val java_version: String by project
val mappings_channel: String by project
val mappings_version: String by project
val vendor: String by project
val github: String by project
val mod_name: String by project
val mod_url: String by project


fun Project.fileTree(dir: String, include: String) = fileTree("dir" to dir, "include" to include)

plugins {
    `maven-publish`
    eclipse
    idea
    java
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
}

//--------------

group = mod_group
version = "${mc_version}-${mod_version}"
base {
    archivesName.set(mod_id)
}

if (System.getenv("RELEASE_TYPE") != null) {
    status = System.getenv("RELEASE_TYPE").lowercase()
    if (status == "snapshot") status = (status as String).uppercase()
} else {
    status = "SNAPSHOT"
}

if (status != "release") {
    version = "${version}-${status}"
}

java {
    withSourcesJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(java_version))
}

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
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
    minecraft("net.minecraftforge:forge:${mc_version}-${forge_version}")
    compileOnly(fg.deobf("mezz.jei:jei-1.20-common-api:${jei_version}"))
    runtimeOnly(fg.deobf("mezz.jei:jei-1.20-forge:${jei_version}"))
    implementation("org.jetbrains:annotations:23.0.0")
}

minecraft {
    mappings(mappings_channel, mappings_version)
    copyIdeResources.set(true)
    runs {
        create("client") {
            workingDirectory(file("run"))
            property("forge.logging.console.level", "debug")
            mods.register("potionbundles") {
                source(sourceSets.getByName("main"))
            }
        }
        create("server") {
            workingDirectory(file("run"))
            property("forge.logging.console.level", "debug")
            mods.register("potionbundles") {
                source(sourceSets.getByName("main"))
            }
        }
        create("data") {
            workingDirectory(file("run"))
            property("forge.logging.console.level", "debug")
            args("--mod", mod_id, "--all", "--output", file("src/main/generated/"), "--existing", file("src/main/resources/"))
            mods.register("potionbundles") {
                source(sourceSets.getByName("main"))
            }
        }
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    processResources {
        val buildProps = project.properties.toMutableMap()
        buildProps.values.removeIf { it !is CharSequence && it !is Number && it !is Boolean }
        inputs.properties(buildProps)

        filesMatching("META-INF/mods.toml") {
            expand(buildProps)
        }
        // minify json files
        doLast {
            fileTree(dir = outputs.files.asPath, include = "**/*.json").forEach {
                it.writeText(JsonOutput.toJson(JsonSlurper().parse(it)))
            }
        }
    }
    named<Jar>("jar") {
        from(sourceSets.main.map { it.output })
        finalizedBy("reobfJar")
    }
    named<Jar>("sourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.map { it.allSource })
    }
    withType<Jar>().configureEach {
        var extension = ""
        if (archiveClassifier.isPresent) {
            extension = archiveClassifier.get()
            if (extension != "") {
                extension = "-$extension"
            }
        }
        manifest {
            attributes(mapOf(
                "Maven-Artifact" to "${mod_group}:${base.archivesName}:${project.version}",
                "Specification-Title" to base.archivesName,
                "Specification-Vendor" to vendor,
                "Specification-Version" to "1",
                "Implementation-Title" to "${base.archivesName}${extension}",
                "Implementation-Version" to mod_version,
                "Implementation-Vendor" to vendor,
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On" to "${mc_version}-${forge_version}",
                "Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                "FMLModType" to "MOD"
            ))
        }
    }
    register("reobf") {
        dependsOn(named("reobfJar"))
    }
    register("setupGithubActions") {
        doLast {
            println("##[set-output name=modid;]${mod_id}")
            println("##[set-output name=version;]${project.version}")
        }
    }
    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}

artifacts {
    archives(tasks.named("jar"))
    archives(tasks.named("sourcesJar"))
}

reobf {
    create("jar") {
        classpath.from(sourceSets.main.map { it.compileClasspath })
    }
}

idea.module.excludeDirs.addAll(arrayOf("run", "out", "libs").map(::file))

publishing {
    publications.create<MavenPublication>("${base.archivesName.get()}ToMaven") {
        groupId = project.group as String
        artifactId = base.archivesName.get()
        version = project.version as String
        from(components["java"])
        pom {
            name.set(mod_name)
            url.set(mod_url)
            packaging = "jar"
            scm {
                connection.set("scm:git:git://github.com/${github}.git")
                developerConnection.set("scm:git:git@github.com:${github}.git")
                url.set("https://github.com/${github}")
            }
            issueManagement {
                system.set("github")
                url.set("https://github.com/${github}/issues")
            }
            organization {
                name.set("Minecraftschurli Mods")
                url.set("https://github.com/MinecraftschurliMods")
            }
            developers {
                developer {
                    id.set("minecraftschurli")
                    name.set("Minecraftschurli")
                    url.set("https://github.com/Minecraftschurli")
                    email.set("minecraftschurli@gmail.com")
                    organization.set("Minecraftschurli Mods")
                    organizationUrl.set("https://github.com/MinecraftschurliMods")
                    timezone.set("Europe/Vienna")
                }
                developer {
                    id.set("ichhabehunger54")
                    name.set("IchHabeHunger54")
                    url.set("https://github.com/IchHabeHunger54")
                    organization.set("Minecraftschurli Mods")
                    organizationUrl.set("https://github.com/MinecraftschurliMods")
                    timezone.set("Europe/Vienna")
                }
            }
            withXml {
                val rootNode = asNode()
                val dependencies: NodeList = (rootNode.get("dependencies") as NodeList)
                val dependencyList = dependencies.getAt("dependency")
                for (dependency in dependencyList) {
                    val dependencyNode = dependency as Node
                    val version = ((((dependencyNode.get("version") as NodeList).last() as Node).value() as NodeList).last() as String)
                    if (version.contains("_mapped_")) {
                        assert(dependencyNode.parent().remove(dependencyNode))
                    }
                }
            }
        }
    }
    repositories {
        maven {
            if ((System.getenv("MAVEN_USER") != null)
                    && (System.getenv("MAVEN_PASSWORD") != null)
                    && (System.getenv("MAVEN_URL") != null)) {
                url = uri(System.getenv("MAVEN_URL"))
                credentials {
                    username = System.getenv("MAVEN_USER")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            } else {
                url = uri("$buildDir/repo")
            }
        }
    }
}
