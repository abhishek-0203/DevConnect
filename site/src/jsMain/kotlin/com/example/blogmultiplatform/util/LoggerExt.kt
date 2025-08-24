package com.example.blogmultiplatform.util

import kotlinx.browser.window
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Headers
import kotlinx.coroutines.await

private suspend fun sendLog(level: String, message: String, file: String = "jsMain", function: String = "unknown") {
    try {
        val headers = Headers()
        headers.append("Content-Type", "application/json")
        val logPayload = js("({})")
        logPayload.level = level
        logPayload.message = message
        logPayload.file = file
        logPayload.function = function
        val body = JSON.stringify(logPayload)
        val requestInit = RequestInit(
            method = "POST",
            headers = headers,
            body = body
        )
        window.fetch("/api/log", requestInit).await()
    } catch (e: dynamic) {
        console.error("Failed to send log to backend: $e")
    }
}

suspend fun logInfo(message: String, file: String = "jsMain", function: String = "unknown") {
    console.log("[INFO] $message")
    sendLog("INFO", message, file, function)
}

suspend fun logError(message: String, file: String = "jsMain", function: String = "unknown") {
    console.error("[ERROR] $message")
    sendLog("ERROR", message, file, function)
}
