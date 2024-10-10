plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

group = "com.pandus.llm.rag"
version = "0.0.1"

val logbackVersion: String by extra
val ktorVersion: String by extra
val kotlinxSerializationVersion: String by extra
val junitTestVersion: String by extra
val langChain4jVersion: String by extra
val chromaVersion: String by extra
val pdfBoxVersion: String by extra
val koinVersion: String by extra

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.pandus.llm.rag.RagKotlinApplicationKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.pandus.llm.rag.RagKotlinApplicationKt")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$junitTestVersion")
    // LangChain4J
    implementation("dev.langchain4j:langchain4j:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-azure-open-ai:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-hugging-face:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-ollama:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-open-ai:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-chroma:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-open-ai:$langChain4jVersion")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika:$langChain4jVersion")
    //Koin
    implementation("io.insert-koin:koin-ktor:$koinVersion")

}

