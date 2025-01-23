dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()

        maven(url = "https://repo.papermc.io/repository/maven-public/")

        maven {
            url = uri("https://nexus.virtusdevelops.eu/repository/maven-releases/")
            isAllowInsecureProtocol = false
            credentials {
                username = System.getenv("NEXUS1_USERNAME")
                    ?: extra["nexusUser"]?.toString()
                            ?: error("Invalid nexus user")
                password = System.getenv("NEXUS1_PASSWORD")
                    ?: extra["nexusPassword"]?.toString()
                            ?: error("Invalid nexus password")
            }
        }

        maven (url = "https://oss.sonatype.org/content/groups/public/")
        maven (url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven (url = "https://jitpack.io")
        maven (url = "https://repo.auxilor.io/repository/maven-public/")


        mavenLocal()
    }
}


include(":api")
include(":plugin")
include("core")
