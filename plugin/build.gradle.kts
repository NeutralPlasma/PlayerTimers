plugins {
    id("java")
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "eu.virtusdevelops"
version = "1.0"

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly ("com.zaxxer:HikariCP:5.0.1")
    compileOnly ("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly ("org.incendo:cloud-core:2.0.0")
    compileOnly ("org.incendo:cloud-annotations:2.0.0")
    compileOnly ("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly ("org.incendo:cloud-minecraft-extras:2.0.0-beta.10")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}



bukkit {
    name = "PlayerTimers"
    main = "eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin"
    apiVersion = "1.20"
    foliaSupported = true
    authors = listOf("NeutralPlasma")
    depend = listOf("PlaceholderAPI")
    libraries = listOf(
        "org.incendo:cloud-core:2.0.0",
        "org.incendo:cloud-annotations:2.0.0",
        "org.incendo:cloud-paper:2.0.0-beta.10",
        "org.incendo:cloud-minecraft-extras:2.0.0-beta.10",
        "net.kyori:adventure-platform-bukkit:4.3.4",
        "com.zaxxer:HikariCP:5.0.1"
    )
}

tasks{
    shadowJar{
        archiveClassifier.set("")
        archiveBaseName.set("PlayerTimers")
    }

    build {
        dependsOn(shadowJar)
    }
}

