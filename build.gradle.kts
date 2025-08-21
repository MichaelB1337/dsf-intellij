import java.io.File

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "dev.michaelbergmann.dsfintellij"
version = "0.0.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

sourceSets {
    named("main") {
        java.srcDirs("src/main/kotlin", "src/main/gen")
    }
}

// --- Read plugin description from README.md ---
fun readPluginDescriptionFromReadme(readmeFile: File): String {
    val start = "<!-- Plugin description -->"
    val end = "<!-- Plugin description end -->"
    val lines = readmeFile.readLines()
    val startIdx = lines.indexOfFirst { it.contains(start) }
    val endIdx = lines.indexOfFirst { it.contains(end) }
    require(startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
        "README.md must contain \"$start\" and \"$end\" markers"
    }
    return lines.subList(startIdx + 1, endIdx).joinToString("\n").trim()
}

val pluginDescription: Provider<String> = providers.provider {
    readPluginDescriptionFromReadme(layout.projectDirectory.file("README.md").asFile)
}

// Configure Gradle IntelliJ Plugin
dependencies {
    intellijPlatform {
        create("IC", "2025.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        description = pluginDescription

        changeNotes = """
            Initial version
            <ul>
                <li>Syntax highlighting for .dsf</li>
                <li>Directive and attribute coloring</li>
                <li>Auto file association</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}