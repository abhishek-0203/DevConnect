package com.example.blogmultiplatform.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.setBodyText
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LogRequest(val level: String, val message: String, val file: String, val function: String)

@Api(routeOverride = "log")
suspend fun logApi(context: ApiContext) {
    try {
        val body = context.req.body?.decodeToString() ?: ""
        val logRequest = Json.decodeFromString<LogRequest>(body)
        val timestamp = java.time.LocalDateTime.now().withSecond(0).withNano(0)
        val logLine = "[${timestamp}] [${logRequest.file}::${logRequest.function}] ${logRequest.level}: ${logRequest.message}\n"
        val logFile = File("site.log")
        logFile.appendText(logLine)
        context.res.setBodyText("Logged")
    } catch (e: Exception) {
        context.res.setBodyText("Error logging: ${e.message}")
    }
}

