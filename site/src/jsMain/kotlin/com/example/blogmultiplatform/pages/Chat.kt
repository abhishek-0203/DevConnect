package com.example.blogmultiplatform.pages

import androidx.compose.runtime.*
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.logInfo
import com.example.shared.Message
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.streams.ApiStream
import com.varabyte.kobweb.streams.connect
import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Text

val ChatBoxStyle by ComponentStyle {
    Modifier
        .padding(5.px)
        .borderRadius(5.px)
        .border { style(LineStyle.Solid) }
        .overflow { y(Overflow.Auto) }
}

private fun Message.toChatLine() = "${this.username}: ${this.text}"

@Page
@Composable
fun ChatPage() {
    isUserLoggedIn {
        ChatScreen()
    }
}

@Composable
fun ChatScreen() {
    val username = remember { localStorage.getItem("username") ?: "" }
    val messages = remember { mutableStateListOf<Message>() }
    val chatStream = remember { ApiStream("chat") }
    LaunchedEffect(Unit) {
        chatStream.connect { ctx ->
            val msg = Json.decodeFromString<Message>(ctx.text)
            if (messages.none { it.username == msg.username && it.text == msg.text }) {
                messages.add(msg)
            }
        }
    }

    Column {
        Column(
            ChatBoxStyle.toModifier().height(80.percent).width(600.px).fontSize(22.px)
        ) {
            LaunchedEffect(Unit) {
                logInfo("", file = "ChatScreen.kt", function = "")
            }
            messages.forEach { entry ->
                LaunchedEffect(entry) {
                    logInfo("$entry", file = "ChatScreen.kt", function = "")
                }
                Text(entry.toChatLine())
                Br()
            }
        }
        Box(ChatBoxStyle.toModifier().width(600.px).height(60.px)) {
            var message by remember { mutableStateOf("") }
            fun sendMessage() {
                val messageCopy = Message(username, message.trim())
                if (messageCopy.username.isNotBlank() && messageCopy.text.isNotBlank()) {
                    chatStream.send(Json.encodeToString<Message>(messageCopy))
                    message = ""
                }
            }
            TextInput(
                message,
                { message = it },
                Modifier.width(70.percent).align(Alignment.BottomStart),
                ref = ref { it.focus() },
                onCommit = ::sendMessage
            )
            Button(modifier = Modifier.width(20.percent).align(Alignment.BottomEnd),
                onClick = { sendMessage() },
                enabled = message.isNotBlank(),
            ) {
                Text("Send")
            }
        }
    }
}
