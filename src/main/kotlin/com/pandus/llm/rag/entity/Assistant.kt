package com.pandus.llm.rag.entity

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface Assistant {
    @SystemMessage("You are friend, answer me using slang.")
    fun chat(@UserMessage message: String, @MemoryId chatId: String): String
}
