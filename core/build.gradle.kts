plugins {
    id("java")
}

group = "eu.virtusdevelops"
version = "0.0.1"


val minecraftVersion: String = "1.21.1"

dependencies {
    compileOnly (project(":api"))
    compileOnly ("com.zaxxer:HikariCP:5.0.1")
    compileOnly ("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly ("me.clip:placeholderapi:2.11.6")
}

tasks.test {
    useJUnitPlatform()
}