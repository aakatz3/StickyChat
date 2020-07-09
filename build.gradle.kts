
import kr.entree.spigradle.kotlin.paper

plugins {
    kotlin("jvm") version "1.3.72"

    id("kr.entree.spigradle") version "2.0.1"
    id("com.github.johnrengelman.shadow") version "5.2.0"

    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "com.dumbdogdiner"
version = "1.0.0"

repositories {
    mavenCentral()

    maven { url = uri("http://nexus.okkero.com/repository/maven-releases/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-core",
        version = "1.3.7"
    )

    shadow(paper("1.16.1"))

    implementation("com.okkero.skedule:skedule:1.2.6")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("net.luckperms:api:5.0")

    shadow("me.clip:placeholderapi:2.10.6")
}

tasks {
    ktlintKotlinScriptCheck {
        dependsOn("ktlintFormat")
    }

    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
        project.configurations.implementation.configure { isCanBeResolved = true }
        project.configurations.shadow.configure { isCanBeResolved = true }

        configurations = listOf(
            project.configurations.implementation.get(),
            project.configurations.shadow.get()
        )
    }

    spigot {
        authors = listOf("SkyezerFox")
        depends = listOf("LuckPerms")
        softDepends = listOf("PlaceholderAPI")
        commands {}
        apiVersion = "1.16"
    }
}
