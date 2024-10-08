package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.entity.LLMType
import io.ktor.server.application.*

data class AppConfig(
    val azureConfig: AzureConfig,
    val huggingFace: HuggingFaceConfig,
    val openAI: OpenAIConfig,
    val localAI: LocalLLMConfig,
    val chromaConfig: ChromaConfig,
    val modelConfig: ModelConfig
)

data class AzureConfig(
    val endpoint: String,
    val apiKey: String,
    val deploymentName: String,
    val tokenizerName: String
)

data class OpenAIConfig(
    val apiKey: String,
    val modelName: String
)

data class HuggingFaceConfig(
    val modelName: String,
    val apiKey: String,
    val embeddingModel: String
)

data class LocalLLMConfig(
    val baseUrl: String,
    val modelName: String,
    val embeddingModelName: String,
    val tokenizerName: String
)

data class ChromaConfig(
    val baseUrl: String,
    val collectionName: String
)

data class ModelConfig(
    val type: LLMType,
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
        localAI = LocalLLMConfig(
            baseUrl = environment.getProperties("myapp.local_ai.base_url"),
            modelName = environment.getProperties("myapp.local_ai.model_name"),
            tokenizerName = environment.getProperties("myapp.local_ai.tokenizer_name"),
            embeddingModelName = environment.getProperties("myapp.local_ai.embedding_model_name")
        ),
        huggingFace = HuggingFaceConfig(
            modelName = environment.getProperties("myapp.hugging_face.model_name"),
            apiKey = environment.getProperties("myapp.hugging_face.api_key"),
            embeddingModel = environment.getProperties("myapp.hugging_face.embedding_model")
        ),
        azureConfig = AzureConfig(
            endpoint = environment.getProperties("myapp.azure.endpoint"),
            apiKey = environment.getProperties("myapp.azure.api_key"),
            deploymentName = environment.getProperties("myapp.azure.deployment_name"),
            tokenizerName = environment.getProperties("myapp.azure.tokenizer_name")
        ),
        chromaConfig = ChromaConfig(
            baseUrl = environment.getProperties("myapp.chroma.base_url"),
            collectionName = environment.getProperties("myapp.chroma.collection_name")
        ),
        modelConfig = ModelConfig(
            type = LLMType.fromValue(environment.getProperties("myapp.llm.type")),
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
        ),
        openAI = OpenAIConfig(
            apiKey = environment.getProperties("myapp.open_ai.api_key"),
            modelName = environment.getProperties("myapp.open_ai.model_name")
        )
    )
}

fun ApplicationEnvironment.getProperties(value: String) = config.property(value).getString()
