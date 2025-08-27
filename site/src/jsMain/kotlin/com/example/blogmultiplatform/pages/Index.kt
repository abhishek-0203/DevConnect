package com.example.blogmultiplatform.pages

import androidx.compose.runtime.*
import com.example.blogmultiplatform.components.CategoryNavigationItems
import com.example.blogmultiplatform.components.OverflowSidePanel
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.Constants.POSTS_PER_PAGE
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.pages.admin.HomeScreen
import com.example.blogmultiplatform.sections.FooterSection
import com.example.blogmultiplatform.sections.HeaderSection
import com.example.blogmultiplatform.sections.MainSection
import com.example.blogmultiplatform.sections.NewsletterSection
import com.example.blogmultiplatform.sections.PostsSection
import com.example.blogmultiplatform.sections.SponsoredPostsSection
import com.example.blogmultiplatform.util.fetchLatestPosts
import com.example.blogmultiplatform.util.fetchMainPosts
import com.example.blogmultiplatform.util.fetchPopularPosts
import com.example.blogmultiplatform.util.fetchSponsoredPosts
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.logInfo
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.color
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.css.style



@Page
@Composable
fun HomePage1() {

    isUserLoggedIn {
        HomeScreen()
    }
}

@Composable
fun HomeScreen1() {
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    var overflowOpened by remember { mutableStateOf(false) }
    var mainPosts by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    val latestPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val sponsoredPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val popularPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    var latestPostsToSkip by remember { mutableStateOf(0) }
    var popularPostsToSkip by remember { mutableStateOf(0) }
    var showMoreLatest by remember { mutableStateOf(false) }
    var showMorePopular by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        fetchMainPosts(
            onSuccess = { mainPosts = it },
            onError = {}
        )
        fetchLatestPosts(
            skip = latestPostsToSkip,
            onSuccess = { response ->
                if (response is ApiListResponse.Success) {
                    latestPosts.addAll(response.data)
                    latestPostsToSkip += POSTS_PER_PAGE
                    if (response.data.size >= POSTS_PER_PAGE) showMoreLatest = true
                }
            },
            onError = {}
        )
        fetchSponsoredPosts(
            onSuccess = { response ->
                if (response is ApiListResponse.Success) {
                    sponsoredPosts.addAll(response.data)
                }
            },
            onError = {}
        )
        fetchPopularPosts(
            skip = popularPostsToSkip,
            onSuccess = { response ->
                if (response is ApiListResponse.Success) {
                    popularPosts.addAll(response.data)
                    popularPostsToSkip += POSTS_PER_PAGE
                    if (response.data.size >= POSTS_PER_PAGE) showMorePopular = true
                }
            },
            onError = {}
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (overflowOpened) {
            OverflowSidePanel(
                onMenuClose = { overflowOpened = false },
                content = { CategoryNavigationItems(vertical = true) }
            )
        }
        HeaderSection(
            breakpoint = breakpoint,
            onMenuOpen = { overflowOpened = true }
        )
        MainSection(
            breakpoint = breakpoint,
            posts = mainPosts,
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        PostsSection(
            breakpoint = breakpoint,
            posts = latestPosts,
            title = "Latest Posts",
            showMoreVisibility = showMoreLatest,
            onShowMore = {
                scope.launch {
                    fetchLatestPosts(
                        skip = latestPostsToSkip,
                        onSuccess = { response ->
                            if (response is ApiListResponse.Success) {
                                if (response.data.isNotEmpty()) {
                                    if (response.data.size < POSTS_PER_PAGE) {
                                        showMoreLatest = false
                                    }
                                    latestPosts.addAll(response.data)
                                    latestPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMoreLatest = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        SponsoredPostsSection(
            breakpoint = breakpoint,
            posts = sponsoredPosts,
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        PostsSection(
            breakpoint = breakpoint,
            posts = popularPosts,
            title = "Popular Posts",
            showMoreVisibility = showMorePopular,
            onShowMore = {
                scope.launch {
                    fetchPopularPosts(
                        skip = popularPostsToSkip,
                        onSuccess = { response ->
                            if (response is ApiListResponse.Success) {
                                if (response.data.isNotEmpty()) {
                                    if (response.data.size < POSTS_PER_PAGE) {
                                        showMorePopular = false
                                    }
                                    popularPosts.addAll(response.data)
                                    popularPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMorePopular = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        NewsletterSection(breakpoint = breakpoint)
        FooterSection()
        FloatingActionButton {
            context.router.navigateTo(Screen.Chat.route)
        }
    }
}

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    val context = rememberPageContext()

    Box(
        modifier = Modifier
            .position(Position.Fixed)
            .right(96.px)
            .bottom(96.px)
            .size(56.px)
            .backgroundColor(com.varabyte.kobweb.compose.ui.graphics.Colors.Blue)
            .borderRadius(28.px)
            .onClick {
                onClick()
                     },
        contentAlignment = Alignment.Center
    ) {
        SpanText(
            text = "+",
            modifier = Modifier
                .backgroundColor(com.varabyte.kobweb.compose.ui.graphics.Colors.Transparent)
                .fontSize(32.px)
                .color(com.varabyte.kobweb.compose.ui.graphics.Colors.White)
        )
    }
}