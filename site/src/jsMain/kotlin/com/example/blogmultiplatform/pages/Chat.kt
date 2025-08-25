package com.example.blogmultiplatform.pages

import androidx.compose.runtime.*
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.logInfo
import com.example.shared.Message
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.*
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
import org.jetbrains.compose.web.attributes.ref
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
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
    val chatBoxRef = remember { mutableStateOf<org.w3c.dom.HTMLElement?>(null) }
    LaunchedEffect(Unit) {
        chatStream.connect { ctx ->
            val msg = Json.decodeFromString<Message>(ctx.text)
            if (messages.none { it.username == msg.username && it.text == msg.text }) {
                messages.add(msg)
            }
        }
    }

    Box(Modifier.fillMaxSize().backgroundColor(Color("#F5F6FA"))) {
        Column(
            Modifier.align(Alignment.Center)
                .width(600.px)
                .height(80.vh) // Set fixed height for the chat area
        ) {
            Box(Modifier.fillMaxWidth().margin(bottom = 16.px).align(Alignment.CenterHorizontally)) {
                Text("Live Chat")
            }
            Div(
                attrs = {
                    attr("id", "chatBox")
                    style {
                        property("height", "100%") // Fill parent height
                        property("width", "600px")
                        property("font-size", "18px")
                        property("overflow-y", "auto")
                        property("background-color", "#FFFFFF")
                        property("box-shadow", "0px 2px 12px 0px #dcdde1")
                        property("border-radius", "16px")
                        property("padding", "16px")
                        property("display", "flex")
                        property("flex-direction", "column")
                    }
                }
            ) {
                Column {
                    messages.forEach { entry ->
                        val isOwnMessage = entry.username == username
                        Row(
                            modifier = Modifier.fillMaxWidth().margin(bottom = 12.px),
                            horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .backgroundColor(Color(if (isOwnMessage) "#4cd137" else "#f5f6fa"))
                                    .borderRadius(16.px)
                                    .padding(12.px)
                                    .maxWidth(60.percent)
                                    .boxShadow(0.px, 2.px, 8.px, 0.px, Color("#dcdde1"))
                            ) {
                                Column {
                                    Box(Modifier.margin(bottom = 6.px)) {
                                        Text(entry.username)
                                    }
                                    Box(Modifier.margin(top = 6.px)) {
                                        Text(entry.text)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LaunchedEffect(messages.size) {
                val box = kotlinx.browser.document.getElementById("chatBox") as? org.w3c.dom.HTMLElement
                box?.scrollTop = box?.scrollHeight?.toDouble() ?: 0.0
            }
            // Input box outside the scrollable Div, at the bottom
            Box(Modifier.width(600.px).height(80.px).margin(top = 16.px).backgroundColor(Color("#FFFFFF")).borderRadius(16.px).boxShadow(0.px, 2.px, 8.px, 0.px, Color("#dcdde1")).padding(12.px)) {
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
                    Modifier.width(70.percent).align(Alignment.CenterStart).fontSize(16.px).backgroundColor(Color("#f5f6fa")).borderRadius(8.px).padding(8.px),
                    ref = ref { it.focus() },
                    onCommit = ::sendMessage
                )
                Button(modifier = Modifier.width(20.percent).align(Alignment.CenterEnd).backgroundColor(Color("#44bd32")).color(Color("#fff")).fontSize(16.px).borderRadius(8.px).padding(8.px),
                    onClick = { sendMessage() },
                    enabled = message.isNotBlank(),
                ) {
                    Text("Send")
                }
            }
        }
    }
}
