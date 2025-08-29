package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.navigation.Screen.SignUp
import com.example.blogmultiplatform.navigation.Type
import com.example.blogmultiplatform.styles.LoginInputStyle
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.LoggerImpl
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.checkUserExistence
import com.example.blogmultiplatform.util.noBorder
import com.example.blogmultiplatform.util.logError
import com.example.blogmultiplatform.util.logInfo
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
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.letterSpacing
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
import kotlinx.browser.localStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.set
import com.example.blogmultiplatform.theme.JsTheme

@Page
@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    var errorText by remember { mutableStateOf(" ") }
    var isDeveloper by remember { mutableStateOf(false) }
    var isClient by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(leftRight = 50.px, top = 80.px, bottom = 24.px)
                .backgroundColor(JsTheme.LightGray),
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
                    .id(Id.usernameInput)
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(14.px)
                    .outline(
                        width = 0.px,
                        style = LineStyle.None,
                        color = Colors.Transparent
                    )
                    .toAttrs {
                        attr("placeholder", "Username")
                    }
            )
            Input(
                type = InputType.Password,
                attrs = LoginInputStyle.toModifier()
                    .id(Id.passwordInput)
                    .margin(bottom = 20.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(14.px)
                    .outline(
                        width = 0.px,
                        style = LineStyle.None,
                        color = Colors.Transparent
                    )
                    .toAttrs {
                        attr("placeholder", "Password")
                    }
            )
            // Move Developer/Client radio row above SignIn
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
                            onInput { event ->
                                isDeveloper = true
                                isClient = false
                            }
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
                            onInput { event ->
                                isClient = true
                                isDeveloper = false
                            }
                        }
                    )
                    SpanText(text = "Client", modifier = Modifier.fontSize(16.px))
                }
            }
            Box(modifier = Modifier.width(350.px).margin(bottom = 12.px), contentAlignment = Alignment.Center) {
                SpanText(
                    text = "SignIn",
                    modifier = Modifier
                        .padding(top = 8.px, bottom = 8.px, leftRight = 0.px)
                        .width(350.px)
                        .backgroundColor(JsTheme.Primary)
                        .color(Colors.White)
                        .fontSize(18.px)
                        .fontWeight(FontWeight.Bold)
                        .borderRadius(8.px)
                        .cursor(if (isDeveloper || isClient) Cursor.Pointer else Cursor.NotAllowed)
                        .textAlign(TextAlign.Center)
                        .onClick {
                            scope.launch {
                                val username = (document.getElementById(Id.usernameInput) as HTMLInputElement).value
                                val password = (document.getElementById(Id.passwordInput) as HTMLInputElement).value
                                val role = if (isDeveloper) Type.Developer.type else if (isClient) Type.Client.type else ""
                                if (role.isEmpty()) {
                                    errorText = "Please select Developer or Client before signing in."
                                    delay(3000)
                                    errorText = " "
                                    return@launch
                                }
                                logInfo("Login action started for username: $username", file = "Login.kt", function = "onClick")
                                val user = checkUserExistence(
                                    user = User(
                                        username = username,
                                        password = password,
                                        role = role
                                    )
                                )
                                if (user != null && user._id.isNotEmpty()) {
                                    // It saves userId, username, role in localStorage
                                    rememberLoggedIn(remember = true, user = user, role = role)
                                    logInfo("Remember me set for file = Login.kt, username: $username, role: $role")

                                    // Navigate based on role, AdminHome for Client, HomePage for Developer
                                    if (user.role == Type.Client.type) {
                                        context.router.navigateTo(Screen.AdminHome.route)
                                    }
                                    else {
                                        context.router.navigateTo(Screen.HomePage.route)
                                    }
                                } else {
                                    logError("Login failed for username: $username - user does not exist.", file = "Login.kt", function = "onClick")
                                    errorText = "The user doesn't exist or credentials are incorrect."
                                    delay(3000)
                                    errorText = " "
                                }
                            }
                        }
                )
            }
            Box(modifier = Modifier.width(350.px).margin(bottom = 32.px), contentAlignment = Alignment.Center) {
                SpanText(
                    text = "Create Account",
                    modifier = Modifier
                        .fontSize(20.px)
                        .fontWeight(FontWeight.Bold)
                        .color(JsTheme.Primary)
                        .textAlign(TextAlign.Center)
                        .cursor(Cursor.Pointer)
                        .padding(top = 4.px, bottom = 4.px)
                        .onClick { context.router.navigateTo(SignUp.route) }
                )
            }
            SpanText(
                modifier = Modifier
                    .width(350.px)
                    .color(Colors.Red)
                    .textAlign(TextAlign.Center)
                    .fontFamily(FONT_FAMILY),
                text = errorText
            )
        }
    }
}

private suspend fun rememberLoggedIn(
    remember: Boolean,
    user: UserWithoutPassword? = null,
    role: String
) {
    logInfo("Login success for username: ${user?.username}, userId: ${user?._id}", file = "Login.kt", function = "rememberLoggedIn")
    localStorage["remember"] = remember.toString()
    if (user != null)  {
        localStorage["userId"] = user._id
        localStorage["username"] = user.username
        localStorage["role"] = role
    }
}