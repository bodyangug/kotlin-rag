package com.pandus.llm.rag.factory

import com.pandus.llm.rag.configuration.AppConfig
import com.pandus.llm.rag.entity.LLMType
import dev.langchain4j.model.Tokenizer
import dev.langchain4j.model.azure.AzureOpenAiChatModel
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel
import dev.langchain4j.model.azure.AzureOpenAiTokenizer
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.huggingface.HuggingFaceChatModel
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.model.openai.OpenAiTokenizer
import java.time.Duration

class LanguageModelFactoryImpl(private val appConfig: AppConfig) : ILanguageModelFactory {
    override fun createChatLanguageModel(): ChatLanguageModel {
        return when (appConfig.modelConfig.type) {
            LLMType.AZURE -> {
                AzureOpenAiChatModel.builder()
                    .endpoint(appConfig.azureConfig.endpoint)
                    .apiKey(appConfig.azureConfig.apiKey)
                    .deploymentName(appConfig.azureConfig.deploymentName)
                    .temperature(appConfig.modelConfig.temperature)
                    .build()
            }

            LLMType.HUGGING_FACE -> {
                HuggingFaceChatModel.builder()
                    .temperature(appConfig.modelConfig.temperature)
                    .accessToken(appConfig.huggingFace.apiKey)
                    .modelId(appConfig.huggingFace.modelName)
                    .timeout(Duration.ofMinutes(5))
                    .build()
            }

            LLMType.LOCAL -> {
                OllamaChatModel.builder()
                    .baseUrl(appConfig.localAI.baseUrl)
                    .modelName(appConfig.localAI.modelName)
                    .temperature(appConfig.modelConfig.temperature)
                    .build()
            }
        }
    }

    override fun createEmbeddingModel(): EmbeddingModel {
        return when (appConfig.modelConfig.type) {
            LLMType.AZURE -> {
                AzureOpenAiEmbeddingModel.builder()
                    .endpoint(appConfig.azureConfig.endpoint)
                    .apiKey(appConfig.azureConfig.apiKey)
                    .deploymentName(appConfig.azureConfig.tokenizerName)
                    .build()
            }

            LLMType.HUGGING_FACE -> {
                HuggingFaceEmbeddingModel.builder()
                    .modelId(appConfig.huggingFace.embeddingModel)
                    .accessToken(appConfig.huggingFace.apiKey)
                    .timeout(Duration.ofMinutes(5))
                    .build()
            }

            LLMType.LOCAL -> {
                OllamaEmbeddingModel.builder()
                    .baseUrl(appConfig.localAI.baseUrl)
                    .modelName(appConfig.localAI.embeddingModelName)
                    .build()
            }
        }
    }

    override fun createTokenizer(): Tokenizer {
        return when (appConfig.modelConfig.type) {
            LLMType.AZURE -> {
                AzureOpenAiTokenizer(appConfig.azureConfig.deploymentName)
            }

            LLMType.LOCAL, LLMType.HUGGING_FACE -> {
                OpenAiTokenizer(appConfig.localAI.tokenizerName)
            }
        }
    }
}
