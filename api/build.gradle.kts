plugins {
    id("java")
}

group = "eu.virtusdevelops"
version = "1.0"


val minecraftVersion: String = "1.21.1"

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}


