package com.example.blogmultiplatform.util

interface Logger {
    fun info(message: String, file: String, function: String)
    fun warning(message: String, file: String, function: String)
    fun error(message: String, file: String, function: String)
}

// Extension functions for automatic file/function logging must be implemented in platform-specific code.
