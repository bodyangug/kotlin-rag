package com.pandus.llm.rag.factory

import dev.langchain4j.model.Tokenizer
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.embedding.EmbeddingModel

interface ILanguageModelFactory {
    fun createChatLanguageModel(): ChatLanguageModel
    fun createEmbeddingModel(): EmbeddingModel
    fun createTokenizer(): Tokenizer
}
