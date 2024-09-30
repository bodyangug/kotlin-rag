package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.entity.Assistant
import com.pandus.llm.rag.entity.MetadataFields
import com.pandus.llm.rag.factory.ILanguageModelFactory
import com.pandus.llm.rag.factory.LanguageModelFactoryImpl
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.Tokenizer
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.openai.OpenAiImageModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.rag.query.transformer.DefaultQueryTransformer
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey
import io.ktor.server.application.*
import org.koin.dsl.module

fun appModule(environment: ApplicationEnvironment) = module {
    // Env properties
    single { loadAppConfig(environment) }
    // Factory
    single<ILanguageModelFactory> { LanguageModelFactoryImpl(get()) }
    // LLM from factory
    single {
        get<ILanguageModelFactory>().createChatLanguageModel()
    }
    // Embedding model from factory
    single {
        get<ILanguageModelFactory>().createEmbeddingModel()
    }
    // Tokenizer from factory
    single {
        get<ILanguageModelFactory>().createTokenizer()
    }
    // Vector store
    single {
        val appConfig: AppConfig = get()
        ChromaEmbeddingStore.builder()
            .baseUrl(appConfig.chromaConfig.baseUrl)
            .collectionName(appConfig.chromaConfig.collectionName)
            .build()
    }
    // TextSplitter
    single {
        val appConfig: AppConfig = get()
        val tokenizer: Tokenizer = get()
        DocumentSplitters.recursive(
            appConfig.modelConfig.tokenizer.maxSegmentSizeInTokens,
            appConfig.modelConfig.tokenizer.maxOverlapSizeInTokens,
            tokenizer
        )
    }
    // EmbeddingStoreContentRetriever
    single {
        val appConfig: AppConfig = get()
        val embeddingModel: EmbeddingModel = get()
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
        DefaultQueryTransformer()
    }
    // RetrievalAugmentor
    single {
        val contentRetriever: EmbeddingStoreContentRetriever = get()
        val queryTransformer: DefaultQueryTransformer = get()

        DefaultRetrievalAugmentor.builder()
            .queryTransformer(queryTransformer)
            .contentRetriever(contentRetriever)
            .build()
    }
    // EmbeddingStoreIngestor
    single {
        val embeddingModel: EmbeddingModel = get()
        val embeddingStore: ChromaEmbeddingStore = get()
        val splitter: DocumentSplitter = get()

        EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .documentSplitter(splitter)
            .build()
    }
    // AIService
    single {
        val llm: ChatLanguageModel = get()
        val chatMemory: MessageWindowChatMemory = get()
        val retrievalAugmentor: DefaultRetrievalAugmentor = get()

        AiServices.builder(Assistant::class.java)
            .chatLanguageModel(llm)
            .chatMemoryProvider { chatMemory }
            .retrievalAugmentor(retrievalAugmentor)
            .build()
    }
    single {
        val appConfig: AppConfig = get()
        OpenAiImageModel.builder()
            .apiKey(appConfig.openAI.apiKey)
            .modelName(appConfig.openAI.modelName)
            .build()
    }
}
