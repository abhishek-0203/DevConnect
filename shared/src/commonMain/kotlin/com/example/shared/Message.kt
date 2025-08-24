package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
class Message(val username: String, val text: String)
