package com.pandus.llm.rag.route

import com.pandus.llm.rag.entity.ContextRequest
import com.pandus.llm.rag.entity.UserResponse
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Metadata
import dev.langchain4j.rag.query.Query
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getContextRoute() {
    val contentRetriever: EmbeddingStoreContentRetriever by inject()
    val chatMemory: MessageWindowChatMemory by inject()
    get("/context") {
        val request = call.receive<ContextRequest>()
        val metadata = Metadata(UserMessage(request.query), request.chatId, chatMemory.messages())
        val retrievedDocuments = contentRetriever.retrieve(Query(request.query, metadata)).joinToString(" ,")
        call.respond<UserResponse>(status = HttpStatusCode.OK, UserResponse(request.chatId, retrievedDocuments))
    }
}
