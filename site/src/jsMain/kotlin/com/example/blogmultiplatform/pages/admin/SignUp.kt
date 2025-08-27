package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.*
import com.example.shared.JsTheme
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.navigation.Type
import com.example.blogmultiplatform.styles.LoginInputStyle
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.logError
import com.example.blogmultiplatform.util.logInfo
import com.example.blogmultiplatform.util.registerUser
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.TextArea

@Page
@Composable
fun SignUpScreen() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    var errorText by remember { mutableStateOf(" ") }
    var successText by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isDeveloper by remember { mutableStateOf(false) }
    var isClient by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(leftRight = 50.px, top = 80.px, bottom = 24.px)
                .backgroundColor(JsTheme.LightGray.rgb),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .margin(bottom = 50.px)
                    .width(100.px),
                src = Res.Image.logo,
                alt = "Logo Image"
            )
            Input(
                type = InputType.Text,
                attrs = LoginInputStyle.toModifier()
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Username")
                        onInput { username = it.value }
                    }
            )
//            Input(
//                type = InputType.Email,
//                attrs = LoginInputStyle.toModifier()
//                    .margin(bottom = 12.px)
//                    .width(350.px)
//                    .height(54.px)
//                    .padding(leftRight = 20.px)
//                    .backgroundColor(Colors.White)
//                    .fontFamily(FONT_FAMILY)
//                    .fontSize(14.px)
//                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
//                    .toAttrs {
//                        attr("placeholder", "Email")
//                        onInput { email = it.value }
//                    }
//            )
            Input(
                type = InputType.Password,
                attrs = LoginInputStyle.toModifier()
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Password")
                        onInput { password = it.value }
                    }
            )
            Input(
                type = InputType.Password,
                attrs = LoginInputStyle.toModifier()
                    .margin(bottom = 20.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Confirm Password")
                        onInput { confirmPassword = it.value }
                    }
            )
            Row(
                modifier = Modifier
                    .margin(top = 16.px, bottom = 16.px)
                    .width(350.px)
                    .backgroundColor(Colors.White)
                    .borderRadius(8.px)
                    .padding(top = 12.px, bottom = 12.px, leftRight = 24.px),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Input(
                        type = InputType.Radio,
                        attrs = Modifier.margin(right = 8.px).toAttrs {
                            attr("name", "userType")
                            if (isDeveloper) attr("checked", "checked")
                            onInput { isDeveloper = true; isClient = false }
                        }
                    )
                    SpanText(text = "Developer", modifier = Modifier.margin(right = 24.px).fontSize(16.px))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Input(
                        type = InputType.Radio,
                        attrs = Modifier.margin(right = 8.px).toAttrs {
                            attr("name", "userType")
                            if (isClient) attr("checked", "checked")
                            onInput { isClient = true; isDeveloper = false }
                        }
                    )
                    SpanText(text = "Client", modifier = Modifier.fontSize(16.px))
                }
            }
            Box(modifier = Modifier.width(350.px).margin(bottom = 12.px), contentAlignment = Alignment.Center) {
                SpanText(
                    text = "Create Account",
                    modifier = Modifier
                        .padding(top = 8.px, bottom = 8.px, leftRight = 0.px)
                        .width(350.px)
                        .backgroundColor(JsTheme.Primary.rgb)
                        .color(Colors.White)
                        .fontSize(18.px)
                        .fontWeight(FontWeight.Bold)
                        .borderRadius(8.px)
                        .cursor(if (isDeveloper || isClient) Cursor.Pointer else Cursor.NotAllowed)
                        .textAlign(TextAlign.Center)
                        .onClick {
                            scope.launch {
                                errorText = " "
                                successText = ""
                                if (username.isBlank() || password.isBlank() || confirmPassword.isBlank() || email.isBlank()) {
                                    errorText = "All fields are required."
                                    return@launch
                                }
                                if (password != confirmPassword) {
                                    errorText = "Passwords do not match."
                                    return@launch
                                }
                                if (!isDeveloper && !isClient) {
                                    errorText = "Please select Developer or Client."
                                    return@launch
                                }
                                val role = if (isDeveloper) Type.Developer.type else Type.Client.type
                                val (success, message) = registerUser(username, password, email, role)
                                if (success) {
                                    successText = message
                                    delay(2000)
                                    context.router.navigateTo(Screen.AdminLogin.route)
                                } else {
                                    errorText = message
                                }
                            }
                        }
                )
            }
            if (errorText.isNotBlank() && errorText != " ") {
                SpanText(
                    modifier = Modifier
                        .width(350.px)
                        .color(Colors.Red)
                        .textAlign(TextAlign.Center)
                        .fontFamily(FONT_FAMILY),
                    text = errorText
                )
            }
            if (successText.isNotBlank()) {
                SpanText(
                    modifier = Modifier
                        .width(350.px)
                        .color(Colors.Green)
                        .textAlign(TextAlign.Center)
                        .fontFamily(FONT_FAMILY),
                    text = successText
                )
            }
        }
    }
}
