package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.entity.Assistant
import com.pandus.llm.rag.entity.MetadataFields
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel
import dev.langchain4j.model.azure.AzureOpenAiTokenizer
import dev.langchain4j.model.huggingface.HuggingFaceChatModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer
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
        ChromaDBContainer(appConfig.chromaConfig.imageName).also { it.start() }
    }
    // LLM
    single {
        val appConfig: AppConfig = get()
        HuggingFaceChatModel.builder()
            .temperature(appConfig.modelConfig.temperature)
            .accessToken(appConfig.huggingFace.apiKey)
            .modelId(appConfig.huggingFace.modelName)
            .build()
    }
    // Embedding model
    single {
        val appConfig: AppConfig = get()
        AzureOpenAiEmbeddingModel.builder()
            .endpoint(appConfig.azureConfig.endpoint)
            .apiKey(appConfig.azureConfig.apiKey)
            .deploymentName(appConfig.azureConfig.tokenizerName)
            .logRequestsAndResponses(true)
            .build()
    }
    // Vector store
    single {
        val chroma: ChromaDBContainer = get()
        ChromaEmbeddingStore.builder()
            .baseUrl(chroma.endpoint)
            .collectionName(UUID.randomUUID().toString())
            .logRequests(true)
            .logResponses(true)
            .build()
    }
    // Tokenizer
    single {
        val appConfig: AppConfig = get()
        AzureOpenAiTokenizer(appConfig.azureConfig.deploymentName)
    }
    // TextSplitter
    single {
        val appConfig: AppConfig = get()
        val tokenizer: AzureOpenAiTokenizer = get()
        DocumentSplitters.recursive(
            appConfig.modelConfig.tokenizer.maxSegmentSizeInTokens,
            appConfig.modelConfig.tokenizer.maxOverlapSizeInTokens,
            tokenizer
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
            .minScore(appConfig.modelConfig.embedding.minScore)
            .dynamicFilter { query: Query ->
                val chatId = query.metadata().chatMemoryId().toString()
                return@dynamicFilter metadataKey(MetadataFields.CHAT_ID.value).isEqualTo(chatId)
            }
            .build()
    }
    // ChatMemory
    single {
        val appConfig: AppConfig = get()
        MessageWindowChatMemory.withMaxMessages(appConfig.modelConfig.maxMessages)
    }
    // QueryTransformer
    single {
        val llm: HuggingFaceChatModel = get()

        CompressingQueryTransformer(llm)
    }
    // RetrievalAugmentor
    single {
        val contentRetriever: EmbeddingStoreContentRetriever = get()
        val queryTransformer: CompressingQueryTransformer = get()

        DefaultRetrievalAugmentor.builder()
            .queryTransformer(queryTransformer)
            .contentRetriever(contentRetriever)
            .build()
    }
    // AIService
    single {
        val llm: HuggingFaceChatModel = get()
        val chatMemory: MessageWindowChatMemory = get()
        val retrievalAugmentor: DefaultRetrievalAugmentor = get()

        AiServices.builder(Assistant::class.java)
            .chatLanguageModel(llm)
            .chatMemoryProvider { chatMemory }
            .retrievalAugmentor(retrievalAugmentor)
            .build()
    }
}
