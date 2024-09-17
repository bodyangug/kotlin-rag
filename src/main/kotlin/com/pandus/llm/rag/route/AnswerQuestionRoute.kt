package com.pandus.llm.rag.route

import com.pandus.llm.rag.entity.Assistant
import com.pandus.llm.rag.entity.UserRequest
import com.pandus.llm.rag.entity.UserResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.answerQuestion() {
    val assistant: Assistant by inject()
    post("/question") {
        val request = call.receive<UserRequest>()
        val response = assistant.chat(request.text, request.chatId)
        call.respond<UserResponse>(UserResponse(request.chatId, response))
    }
}
