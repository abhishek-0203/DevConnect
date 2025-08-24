package com.example.blogmultiplatform.pages

import androidx.compose.runtime.*
import com.example.blogmultiplatform.pages.admin.CreateScreen
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.logInfo
import com.example.shared.Message
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.SwitchSize.LG.width
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
import org.w3c.dom.get

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
    val userId = remember { localStorage["userId"] }
    val username = remember { localStorage["username"] }

    /*val account = (LoginState.current as? LoginState.LoggedIn)?.account ?: run {
        LoggedOutMessage()
        return
    }*/

    val messages = remember { mutableStateListOf<Message>() }
    val chatStream = remember { ApiStream("chat") }
    LaunchedEffect(Unit) {
        chatStream.connect { ctx ->
            messages.add(Json.decodeFromString<Message>(ctx.text))
        }
    }

//    CenteredColumnContent {
        Column(
            ChatBoxStyle.toModifier().height(80.percent).width(600.px).fontSize(22.px)
        ) {

            LaunchedEffect(Unit) {
                logInfo("", file = "ChatScreen.kt", function = "")
            }

            messages.forEach { entry ->
                LaunchedEffect(Unit) {
                    logInfo("$entry", file = "ChatScreen.kt", function = "")
                }
                Text(entry.toChatLine())
                Br()
            }
        }
        Box(ChatBoxStyle.toModifier().width(600.px).height(60.px)) {
            var message by remember { mutableStateOf("") }

            fun sendMessage() {
                val messageCopy = Message(username!!, message.trim())
                messages.add(messageCopy)
                message = ""
                chatStream.send(Json.encodeToString<Message>(messageCopy))
            }
            TextInput(
                message,
                { message = it },
                Modifier.width(70.percent).align(Alignment.BottomStart),
                ref = ref { it.focus() },
                onCommit = ::sendMessage
            )
            Button(modifier = Modifier.width(20.percent).align(Alignment.BottomEnd),
                onClick = {::sendMessage},
                enabled = message.isNotBlank(),
            ) {
                Text("Send")
            }
            /*TextButton(
                "Send",
                Modifier.width(20.percent).align(Alignment.BottomEnd),
                enabled = message.isNotBlank(),
                onClick = ::sendMessage
            )*/
        }
//    }
}
