package com.example.blogmultiplatform.styles

import com.example.blogmultiplatform.util.Id
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.TransitionProperty
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import org.jetbrains.compose.web.css.ms

val NavigationItemStyle by ComponentStyle {
    cssRule(" > #${Id.svgParent} > #${Id.vectorIcon}") {
        Modifier
            .transition(CSSTransition(property = TransitionProperty.All, duration = 300.ms))
            .styleModifier {
                property("stroke", "#FFFFFF") // Replace JsTheme.White.hex with direct color
            }
    }
    cssRule(":hover > #${Id.svgParent} > #${Id.vectorIcon}") {
        Modifier
            .styleModifier {
                property("stroke", "#007BFF") // Replace JsTheme.Primary.hex with direct color
            }
    }
    cssRule(" > #${Id.navigationText}") {
        Modifier
            .transition(CSSTransition(property = TransitionProperty.All, duration = 300.ms))
            .color(com.varabyte.kobweb.compose.ui.graphics.Colors.White) // Replace JsTheme.White.rgb
    }
    cssRule(":hover > #${Id.navigationText}") {
        Modifier.color(com.varabyte.kobweb.compose.ui.graphics.Colors.Blue) // Replace JsTheme.Primary.rgb
    }
}