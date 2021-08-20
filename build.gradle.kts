import java.io.ByteArrayOutputStream

plugins {
    java
    id("com.github.hierynomus.license-base") version "0.16.1"
}

project.group = "io.github.emilyy-dev"
project.version = "1.0"
val patchVersion: String = determinePatchVersion()
val fullVersion: String = "${project.version}.$patchVersion"

fun determinePatchVersion(): String {
    val outputStream = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags")
        standardOutput = outputStream
    }
    val output = outputStream.toString(Charsets.UTF_8)
    return if (output.contains('-')) output.split('-')[1] else "0"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.kyori", "adventure-text-serializer-plain", "4.8.1") {
        because("To use the PlainTextComponentSerializer which was introduced in 4.8.0, Paper 1.16.5 uses 4.7.0")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

license {
    header = file("header.txt")
    encoding = "UTF-8"
    mapping("java", "DOUBLESLASH_STYLE")
    include("**/*.java")
}

tasks {
    check { finalizedBy(licenseMain) }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    jar {
        finalizedBy(copyToServer)

        manifest.attributes["Automatic-Module-Name"] = "io.github.emilyydev.chatregexfilter"
        metaInf.from(file("LICENSE.txt")) {
            into("io.github.emilyydev/chatregexfilter")
        }

        archiveVersion.set("v$fullVersion")
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand("version" to fullVersion)
        }
    }
}

task<Copy>("copyToServer") {
    dependsOn(tasks.jar)
    from(tasks.jar.get().archiveFile)
    val pluginsDir: String? = project.property("plugins-dir") as String?
    pluginsDir?.also { destinationDir = file(pluginsDir) }
}
val TaskContainer.copyToServer: TaskProvider<Copy>
    get() = named<Copy>("copyToServer")
