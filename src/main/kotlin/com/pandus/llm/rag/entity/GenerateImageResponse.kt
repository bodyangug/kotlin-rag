package com.pandus.llm.rag.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GenerateImageResponse(
    @SerialName("chat_id")
    val chatId: String,
    val url: String
)
