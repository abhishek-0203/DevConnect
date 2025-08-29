package com.example.blogmultiplatform.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.CategoryNavigationItems
import com.example.blogmultiplatform.components.SearchBar
import com.example.shared.Category
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.util.Constants.HEADER_HEIGHT
import com.example.blogmultiplatform.util.Constants.PAGE_WIDTH
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.Res
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.icons.fa.FaBars
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLInputElement

@Composable
fun HeaderSection(
    breakpoint: Breakpoint,
    selectedCategory: Category? = null,
    logo: String = Res.Image.logoHome,
    onMenuOpen: () -> Unit,
    profileName: String? = null
) {
    var drawerOpened by remember { mutableStateOf(false) }
    val context = rememberPageContext()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Colors.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(PAGE_WIDTH.px),
            contentAlignment = Alignment.TopCenter
        ) {
            Header(
                breakpoint = breakpoint,
                logo = logo,
                selectedCategory = selectedCategory,
                onMenuOpen = onMenuOpen,
                profileName = profileName,
                onDrawerOpen = { drawerOpened = true }
            )
            if (drawerOpened) {
                // Overlay to dim main content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .backgroundColor(Colors.Black)
                        .zIndex(999)
                        .opacity(0.7f)
                        .onClick { drawerOpened = false }
                ) {}
                // Left-side drawer
                ProjectDrawer(
                    onClose = { drawerOpened = false },
                    onHomeClick = {
                        drawerOpened = false
                        context.router.navigateTo("/")
                    },
                    onProfileClick = {
                        drawerOpened = false
                        context.router.navigateTo("/profile")
                    },
                    onLogoutClick = {
                        localStorage.clear()
                        drawerOpened = false
                        context.router.navigateTo("/admin/login")
                    }
                )
            }
        }
    }
}

@Composable
fun Header(
    breakpoint: Breakpoint,
    logo: String,
    selectedCategory: Category?,
    onMenuOpen: () -> Unit,
    profileName: String? = null,
    onDrawerOpen: () -> Unit
) {
    val context = rememberPageContext()
    var fullSearchBarOpened by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth(if (breakpoint > Breakpoint.MD) 80.percent else 90.percent)
            .height(HEADER_HEIGHT.px)
            .backgroundColor(Colors.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (breakpoint <= Breakpoint.MD) {
            if (fullSearchBarOpened) {
                FaXmark(
                    modifier = Modifier
                        .margin(right = 24.px)
                        .color(Colors.White)
                        .cursor(Cursor.Pointer)
                        .onClick { fullSearchBarOpened = false },
                    size = IconSize.XL
                )
            }
            if (!fullSearchBarOpened) {
                FaBars(
                    modifier = Modifier
                        .margin(right = 24.px)
                        .color(Colors.White)
                        .cursor(Cursor.Pointer)
                        .onClick { onMenuOpen() },
                    size = IconSize.XL
                )
            }
        }
        if (!fullSearchBarOpened) {
            Image(
                modifier = Modifier
                    .margin(right = 50.px)
                    .width(if (breakpoint >= Breakpoint.SM) 100.px else 70.px)
                    .cursor(Cursor.Pointer)
                    .onClick { context.router.navigateTo(Screen.HomePage.route) },
                src = logo,
                alt = "Logo Image"
            )
        }
        if (breakpoint >= Breakpoint.LG) {
            CategoryNavigationItems(selectedCategory = selectedCategory)
        }
        Spacer()
        SearchBar(
            breakpoint = breakpoint,
            fullWidth = fullSearchBarOpened,
            darkTheme = true,
            onEnterClick = {
                val query = (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value
                context.router.navigateTo(Screen.SearchPage.searchByTitle(query = query))
            },
            onSearchIconClick = { fullSearchBarOpened = it }
        )
        // Profile Icon and Name
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .margin(left = 12.px, right = 12.px)
                    .width(36.px)
                    .height(36.px)
                    .cursor(Cursor.Pointer)
                    .onClick { context.router.navigateTo(Screen.Profile.route) },
                src = Res.Image.profileIcon,
                alt = "Profile"
            )
            if (!profileName.isNullOrBlank()) {
                SpanText(
                    text = profileName,
                    modifier = Modifier
                        .fontSize(14.px)
                        .color(Colors.White)
                        .margin(top = 4.px)
                )
            }
        }
        // Hamburger icon (3 lines) at the right corner beside profile
        FaBars(
            modifier = Modifier
                .margin(left = 12.px, right = 0.px)
                .color(Colors.White)
                .cursor(Cursor.Pointer)
                .onClick { onDrawerOpen() },
            size = IconSize.LG
        )
    }
}

@Composable
fun ProjectDrawer(
    onClose: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .position(org.jetbrains.compose.web.css.Position.Fixed)
            .right(0.px)
            .top(0.px)
            .height(100.percent)
            .width(250.px)
            .zIndex(1000),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .padding(top = 32.px, leftRight = 32.px),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.margin(bottom = 40.px)
            ) {
                FaXmark(
                    modifier = Modifier
                        .color(Colors.White)
                        .cursor(Cursor.Pointer)
                        .onClick { onClose() },
                    size = IconSize.LG
                )
            }
            // Home
            SpanText(
                text = "Home",
                modifier = Modifier
                    .fontSize(18.px)
                    .color(Colors.White)
                    .margin(bottom = 32.px)
                    .cursor(Cursor.Pointer)
                    .onClick { onHomeClick() }
            )
            // Profile
            SpanText(
                text = "Profile",
                modifier = Modifier
                    .fontSize(18.px)
                    .color(Colors.White)
                    .margin(bottom = 32.px)
                    .cursor(Cursor.Pointer)
                    .onClick { onProfileClick() }
            )
            // Logout
            SpanText(
                text = "Logout",
                modifier = Modifier
                    .fontSize(18.px)
                    .color(Colors.Red)
                    .margin(bottom = 32.px)
                    .cursor(Cursor.Pointer)
                    .onClick {
                        onLogoutClick()
                    }
            )
        }
    }
}
