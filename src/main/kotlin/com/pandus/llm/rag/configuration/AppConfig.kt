package com.pandus.llm.rag.configuration

import io.ktor.server.application.*

data class AppConfig(
    val azureConfig: AzureConfig,
    val huggingFace: HuggingFaceConfig,
    val chromaConfig: ChromaConfig,
    val modelConfig: ModelConfig
)

data class AzureConfig(
    val endpoint: String,
    val apiKey: String,
    val deploymentName: String,
    val tokenizerName: String
)

data class HuggingFaceConfig(
    val modelName: String,
    val apiKey: String,
    val tokenizerName: String
)

data class ChromaConfig(
    val imageName: String
)

data class ModelConfig(
    val maxMessages: Int,
    val maxTokens: Int,
    val tokenizer: TokenizerConfig,
    val embedding: EmbeddingConfig,
    val temperature: Double
)

data class TokenizerConfig(
    val maxSegmentSizeInTokens: Int,
    val maxOverlapSizeInTokens: Int
)

data class EmbeddingConfig(
    val minScore: Double
)

fun loadAppConfig(environment: ApplicationEnvironment): AppConfig {
    return AppConfig(
        huggingFace = HuggingFaceConfig(
            modelName = environment.getProperties("myapp.hugging_face.model_name"),
            apiKey = environment.getProperties("myapp.hugging_face.api_key"),
            tokenizerName = environment.getProperties("myapp.hugging_face.tokenizer_name")
        ),
        azureConfig = AzureConfig(
            endpoint = environment.getProperties("myapp.azure.endpoint"),

            apiKey = environment.getProperties("myapp.azure.api_key"),
            deploymentName = environment.getProperties("myapp.azure.deployment_name"),
            tokenizerName = environment.getProperties("myapp.azure.tokenizer_name")
        ),
        chromaConfig = ChromaConfig(
            imageName = environment.getProperties("myapp.chroma.image_name")
        ),
        modelConfig = ModelConfig(
            maxMessages = environment.getProperties("myapp.llm.chat_memory.max_messages").toInt(),
            tokenizer = TokenizerConfig(
                maxSegmentSizeInTokens = environment.getProperties("myapp.llm.tokenizer.max_segment_size_in_tokens")
                    .toInt(),
                maxOverlapSizeInTokens = environment.getProperties("myapp.llm.tokenizer.max_overlap_size_in_tokens")
                    .toInt()
            ),
            embedding = EmbeddingConfig(
                minScore = environment.getProperties("myapp.llm.embedding.min_score").toDouble()
            ),
            temperature = environment.getProperties("myapp.llm.model.temperature").toDouble(),
            maxTokens = environment.getProperties("myapp.llm.model.max_tokens").toInt()
        )
    )
}

fun ApplicationEnvironment.getProperties(value: String) = config.property(value).getString()
