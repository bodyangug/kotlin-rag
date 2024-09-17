package com.pandus.llm.rag.entity

import dev.langchain4j.service.MemoryId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContextRequest(
    @MemoryId
    @SerialName("chat_id")
    val chatId: String,
    val query: String
)
