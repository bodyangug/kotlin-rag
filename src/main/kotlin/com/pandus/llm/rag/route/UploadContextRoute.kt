package com.pandus.llm.rag.route

import com.pandus.llm.rag.entity.MetadataFields
import com.pandus.llm.rag.entity.UserResponse
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.uploadContext() {
    val ingestor: EmbeddingStoreIngestor by inject()
    post("/upload") {
        val request = call.receiveMultipart()
        val fileParts = mutableListOf<PartData.FileItem>()
        var chatId = ""

        request.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == MetadataFields.CHAT_ID.value) {
                        chatId = part.value
                    }
                }

                is PartData.FileItem -> {
                    fileParts.add(part)
                }

                else -> part.dispose()
            }
        }

        if (chatId.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Missing chat_id")
            return@post
        }

        var responseMessage = "Files uploaded successfully:\n"
        val documents = mutableListOf<Document>()
        for (part in fileParts) {
            val name = part.originalFileName ?: "untitled"
            responseMessage += "$name\n"
            part.streamProvider().use { inputStream ->
                val parsedDocument = ApacheTikaDocumentParser().parse(inputStream)
                val content = parsedDocument.text()
                val metadata = parsedDocument.metadata() ?: Metadata()
                metadata.put(MetadataFields.FILE_NAME.value, name)
                metadata.put(MetadataFields.CHAT_ID.value, chatId)
                val document = Document(content, metadata)
                documents.add(document)
            }
            part.dispose()
        }
        for (document in documents) {
            ingestor.ingest(document)
        }
        call.respond<UserResponse>(status = HttpStatusCode.OK, UserResponse(chatId, responseMessage))
    }
}
