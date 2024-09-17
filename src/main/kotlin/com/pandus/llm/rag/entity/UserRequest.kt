package com.pandus.llm.rag.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    @SerialName("chat_id")
    val chatId: String,
    val text: String
)
