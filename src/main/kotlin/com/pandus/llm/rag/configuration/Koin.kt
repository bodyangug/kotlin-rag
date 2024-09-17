package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.entity.Assistant
import com.pandus.llm.rag.entity.MetadataFields
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.azure.AzureOpenAiChatModel
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel
import dev.langchain4j.model.azure.AzureOpenAiTokenizer
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey
import io.ktor.server.application.*
import org.koin.dsl.module
import org.testcontainers.chromadb.ChromaDBContainer
import java.util.*

fun appModule(environment: ApplicationEnvironment) = module {
    // Env properties
    single { loadAppConfig(environment) }
    // Vector Store
    single {
        val appConfig: AppConfig = get()
        ChromaDBContainer(appConfig.imageName).also { it.start() }
    }
    // LLM
    single {
        val appConfig: AppConfig = get()
        AzureOpenAiChatModel.builder()
            .deploymentName(appConfig.deploymentName)
            .endpoint(appConfig.endpoint)
            .apiKey(appConfig.apiKey)
            .temperature(appConfig.temperature)
            .maxTokens(appConfig.maxLLMTokens)
            .logRequestsAndResponses(true)
            .build()
    }
    // Embedding model
    single {
        val appConfig: AppConfig = get()
        AzureOpenAiEmbeddingModel.builder()
            .endpoint(appConfig.endpoint)
            .apiKey(appConfig.apiKey)
            .deploymentName(appConfig.tokenizerName)
            .build()
    }
    // Vector store
    single {
        val chroma: ChromaDBContainer = get()
        ChromaEmbeddingStore.builder()
            .baseUrl(chroma.endpoint)
            .collectionName(UUID.randomUUID().toString())
            .build()
    }
    // TextSplitter
    single {
        val appConfig: AppConfig = get()
        DocumentSplitters.recursive(
            appConfig.maxSegmentSizeInTokens,
            appConfig.maxOverlapSizeInTokens,
            AzureOpenAiTokenizer(appConfig.deploymentName)
        )
    }
    // EmbeddingStoreIngestor
    single {
        val embeddingModel: AzureOpenAiEmbeddingModel = get()
        val embeddingStore: ChromaEmbeddingStore = get()
        val splitter: DocumentSplitter = get()

        EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .documentSplitter(splitter)
            .build()
    }
    // EmbeddingStoreContentRetriever
    single {
        val appConfig: AppConfig = get()
        val embeddingModel: AzureOpenAiEmbeddingModel = get()
        val embeddingStore: ChromaEmbeddingStore = get()
        EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .minScore(appConfig.embeddingMinScore)
            .dynamicFilter { query: Query ->
                val chatId = query.metadata().chatMemoryId().toString()
                return@dynamicFilter metadataKey(MetadataFields.CHAT_ID.value).isEqualTo(chatId)
            }.build()
    }
    // ChatMemory
    single {
        val appConfig: AppConfig = get()
        MessageWindowChatMemory.withMaxMessages(appConfig.maxMessages)
    }
    // AIService
    single {
        val llm: AzureOpenAiChatModel = get()
        val embeddingStoreContentRetriever: EmbeddingStoreContentRetriever = get()
        val chatMemory: MessageWindowChatMemory = get()

        AiServices.builder(Assistant::class.java)
            .chatLanguageModel(llm)
            .chatMemoryProvider { chatMemory }
            .contentRetriever(embeddingStoreContentRetriever)
            .build()
    }
}
