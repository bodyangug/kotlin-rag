package com.pandus.llm.rag.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GenerateImageRequest(
    @SerialName("chat_id")
    val chatId: String,
    val text: String
)
