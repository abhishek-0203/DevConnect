package com.example.blogmultiplatform.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val userId: String,
    val name: String = "",
    val bio: String = "",
    val contact: String = "",
    val role: String = "" // developer or client
)
