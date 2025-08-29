package com.example.blogmultiplatform.pages

import androidx.compose.runtime.*
import com.example.blogmultiplatform.models.Profile
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.getProfile
import com.example.blogmultiplatform.util.saveProfile
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.localStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input

@Page
@Composable
fun ProfileScreen() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    var errorText by remember { mutableStateOf(" ") }
    var successText by remember { mutableStateOf("") }
    val userId = localStorage.getItem("userId") ?: ""
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    // Load profile on page open
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            val response = getProfile(userId)
            if (response.success && response.profile != null) {
                name = response.profile.name
                bio = response.profile.bio
                contact = response.profile.contact
                role = response.profile.role
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(leftRight = 50.px, top = 80.px, bottom = 24.px)
                .backgroundColor(com.example.shared.JsTheme.LightGray.rgb),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.varabyte.kobweb.silk.components.graphics.Image(
                modifier = Modifier
                    .margin(bottom = 50.px)
                    .width(100.px),
                src = com.example.blogmultiplatform.util.Res.Image.logo,
                alt = "Logo Image"
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Name")
                        onInput { name = it.value }
                    }
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Bio")
                        onInput { bio = it.value }
                    }
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .margin(bottom = 12.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Contact Info")
                        onInput { contact = it.value }
                    }
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .margin(bottom = 20.px)
                    .width(350.px)
                    .height(54.px)
                    .padding(leftRight = 20.px)
                    .backgroundColor(Colors.White)
                    .fontSize(14.px)
                    .outline(width = 0.px, style = LineStyle.None, color = Colors.Transparent)
                    .toAttrs {
                        attr("placeholder", "Role (developer/client)")
                        onInput { role = it.value }
                    }
            )
            Box(modifier = Modifier.width(350.px).margin(bottom = 12.px), contentAlignment = Alignment.Center) {
                SpanText(
                    text = "Save Profile",
                    modifier = Modifier
                        .padding(top = 8.px, bottom = 8.px, leftRight = 0.px)
                        .width(350.px)
                        .backgroundColor(com.example.shared.JsTheme.Primary.rgb)
                        .color(Colors.White)
                        .fontSize(18.px)
                        .fontWeight(FontWeight.Bold)
                        .borderRadius(8.px)
                        .textAlign(com.varabyte.kobweb.compose.css.TextAlign.Center)
                        .onClick {
                            scope.launch {
                                errorText = " "
                                successText = ""
                                if (name.isBlank() || role.isBlank()) {
                                    errorText = "Name and role are required."
                                    return@launch
                                }
                                val profile = Profile(
                                    userId = userId,
                                    name = name,
                                    bio = bio,
                                    contact = contact,
                                    role = role
                                )
                                val response = saveProfile(profile)
                                if (response.success) {
                                    successText = "Profile saved!"
                                    errorText = " "
                                    localStorage.setItem("profileName", name)
                                    context.router.navigateTo(Screen.HomePage.route)
                                } else {
                                    errorText = response.message
                                    successText = ""
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
                        .textAlign(com.varabyte.kobweb.compose.css.TextAlign.Center),
                    text = errorText
                )
            }
            if (successText.isNotBlank()) {
                SpanText(
                    modifier = Modifier
                        .width(350.px)
                        .color(Colors.Green)
                        .textAlign(com.varabyte.kobweb.compose.css.TextAlign.Center),
                    text = successText
                )
            }
        }
    }
}
