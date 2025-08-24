package com.example.blogmultiplatform.api

import com.example.shared.Message
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.stream.ApiStream
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Api(routeOverride = "chat")
val chat = ApiStream { ctx ->
    /*try {
        val message = Json.decodeFromString<Message>(ctx.text)
        if (message.username.isNotBlank() && message.text.isNotBlank()) {
            val jsonMessage = Json.encodeToString(message)
            ctx.stream.broadcast(jsonMessage) { true }
        }
        // Optionally, handle invalid messages (e.g., log or ignore)
    } catch (_: Exception) {
        // Optionally, handle deserialization errors (e.g., log or ignore)
    }*/

    ctx.stream.broadcast(ctx.text) { true }
}
