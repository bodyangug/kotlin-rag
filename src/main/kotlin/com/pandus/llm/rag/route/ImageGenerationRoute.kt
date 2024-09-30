package com.pandus.llm.rag.route

import com.pandus.llm.rag.entity.GenerateImageRequest
import com.pandus.llm.rag.entity.GenerateImageResponse
import dev.langchain4j.model.openai.OpenAiImageModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.generateImage() {
    val imageModel: OpenAiImageModel by inject()
    post("/generate-image") {
        val request = call.receive<GenerateImageRequest>()

        val response = imageModel.generate(request.text)

        call.respond<GenerateImageResponse>(
            HttpStatusCode.OK,
            GenerateImageResponse(chatId = request.chatId, url = response.content().url().toString())
        )
    }
}
