package com.pandus.llm.rag

import com.pandus.llm.rag.configuration.appModule
import com.pandus.llm.rag.configuration.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
    install(Koin) {
        modules(appModule(environment))
    }
}
