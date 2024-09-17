rootProject.name = "rag-kotlin"

pluginManagement {
    val kotlinVersion: String by extra
    val ktorVersion: String by extra

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
    }
}
