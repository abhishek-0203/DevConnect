package com.example.blogmultiplatform.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.theme.JsTheme
import com.example.blogmultiplatform.util.Constants.PAGE_WIDTH
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import org.jetbrains.compose.web.css.px

@Composable
fun AdminPageLayout(content: @Composable () -> Unit) {
    var overflowOpened by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize().backgroundColor(JsTheme.Secondary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .maxWidth(PAGE_WIDTH.px)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .maxWidth(PAGE_WIDTH.px)
                    .backgroundColor(JsTheme.Secondary)
                    .height(60.px),
                contentAlignment = Alignment.CenterStart
            ) {
                // You can add header content here, e.g., Text("Admin Panel")
            }
            SidePanel(onMenuClick = {
                overflowOpened = true
            })
            if (overflowOpened) {
                OverflowSidePanel(
                    onMenuClose = {
                        overflowOpened = false
                    },
                    content = {
                        NavigationItems()
                    }
                )
            }
            content()
        }
    }
}