package com.example.blogmultiplatform.util

import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class LogRequest(val level: String, val message: String, val file: String, val function: String)

class LoggerImpl {
    private fun getCurrentMinuteTimestamp(): String {
        val now = js("new Date()")
        val year = now.getFullYear()
        val month = (now.getMonth() + 1).toString().padStart(2, '0')
        val day = now.getDate().toString().padStart(2, '0')
        val hour = now.getHours().toString().padStart(2, '0')
        val minute = now.getMinutes().toString().padStart(2, '0')
        return "[$year-$month-$day $hour:$minute]"
    }

    private fun sendLog(level: String, message: String, file: String, function: String) {
        val logRequest = LogRequest(level, message, file, function)
        window.fetch("/api/log", object :
            org.w3c.fetch.RequestInit {
            override var method: String? = "POST"
            override var body: dynamic = Json.encodeToString(logRequest)
            override var headers: dynamic = js("({ 'Content-Type': 'application/json' })")
        }
        )
    }

    fun info(message: String, file: String, function: String) {
        console.log("INFO:", "${getCurrentMinuteTimestamp()} [$file::$function] $message")
        sendLog("INFO", message, file, function)
    }
    fun warning(message: String, file: String, function: String) {
        console.warn("WARNING:", "${getCurrentMinuteTimestamp()} [$file::$function] $message")
        sendLog("WARNING", message, file, function)
    }
    fun error(message: String, file: String, function: String) {
        console.error("ERROR:", "${getCurrentMinuteTimestamp()} [$file::$function] $message")
        sendLog("ERROR", message, file, function)
    }
}
