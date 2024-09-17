package com.pandus.llm.rag.configuration

import com.pandus.llm.rag.route.answerQuestion
import com.pandus.llm.rag.route.getContextRoute
import com.pandus.llm.rag.route.uploadContext
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/llm") {
            answerQuestion()
            uploadContext()
            getContextRoute()
        }
    }
}
