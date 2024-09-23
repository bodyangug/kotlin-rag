package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.entity.Assistant
import com.pandus.llm.rag.entity.MetadataFields
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.model.openai.OpenAiTokenizer
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

fun appModule(environment: ApplicationEnvironment) = module {
    // Env properties
    single { loadAppConfig(environment) }
    // LLM
    single {
        val appConfig: AppConfig = get()
        OllamaChatModel.builder()
            .baseUrl(appConfig.localAI.baseUrl)
            .modelName(appConfig.localAI.modelName)
            .temperature(appConfig.modelConfig.temperature)
            .build()
    }
    // Embedding model
    single {
        val appConfig: AppConfig = get()
        OllamaEmbeddingModel.builder()
            .baseUrl(appConfig.localAI.baseUrl)
            .modelName(appConfig.localAI.embeddingModelName)
            .build()
    }
    // Vector store
    single {
        val appConfig: AppConfig = get()
        ChromaEmbeddingStore.builder()
            .baseUrl(appConfig.chromaConfig.baseUrl)
            .collectionName(appConfig.chromaConfig.collectionName)
            .build()
    }
    // Tokenizer
    single {
        val appConfig: AppConfig = get()
        OpenAiTokenizer(appConfig.localAI.tokenizerName)
    }
    // TextSplitter
    single {
        val appConfig: AppConfig = get()
        val tokenizer: OpenAiTokenizer = get()
        DocumentSplitters.recursive(
            appConfig.modelConfig.tokenizer.maxSegmentSizeInTokens,
            appConfig.modelConfig.tokenizer.maxOverlapSizeInTokens,
            tokenizer
        )
    }
    // EmbeddingStoreIngestor
    single {
        val embeddingModel: OllamaEmbeddingModel = get()
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
        val embeddingModel: OllamaEmbeddingModel = get()
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
        val llm: OllamaChatModel = get()
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
        val llm: OllamaChatModel = get()
        val chatMemory: MessageWindowChatMemory = get()
        val retrievalAugmentor: DefaultRetrievalAugmentor = get()

        AiServices.builder(Assistant::class.java)
            .chatLanguageModel(llm)
            .chatMemoryProvider { chatMemory }
            .retrievalAugmentor(retrievalAugmentor)
            .build()
    }
}
