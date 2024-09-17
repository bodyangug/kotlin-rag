package com.pandus.llm.rag.configuration

import io.ktor.server.application.*

data class AppConfig(
    val endpoint: String,
    val apiKey: String,
    val deploymentName: String,
    val tokenizerName: String,
    val imageName: String,
    val maxMessages: Int,
    val maxSegmentSizeInTokens: Int,
    val maxOverlapSizeInTokens: Int,
    val embeddingMinScore: Double,
    val temperature: Double,
    val maxLLMTokens: Int
)

fun loadAppConfig(environment: ApplicationEnvironment): AppConfig {
    val config = environment.config
    return AppConfig(
        endpoint = config.property("myapp.llm.azure.endpoint").getString(),
        apiKey = config.property("myapp.llm.azure.api_key").getString(),
        deploymentName = config.property("myapp.azure.deployment_name").getString(),
        tokenizerName = config.property("myapp.azure.tokenizer_name").getString(),
        imageName = config.property("myapp.chroma.image_name").getString(),
        maxMessages = config.property("myapp.llm.chat_memory.max_messages").getString().toInt(),
        maxSegmentSizeInTokens = config.property("myapp.llm.tokenizer.max_segment_size_in_tokens").getString().toInt(),
        maxOverlapSizeInTokens = config.property("myapp.llm.tokenizer.max_overlap_size_in_tokens").getString().toInt(),
        embeddingMinScore = config.property("myapp.llm.embedding.min_score").getString().toDouble(),
        temperature = config.property("myapp.llm.model.temperature").getString().toDouble(),
        maxLLMTokens = config.property("myapp.llm.model.max_tokens").getString().toInt()
    )
}
