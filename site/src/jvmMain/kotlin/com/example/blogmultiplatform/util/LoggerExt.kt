package com.example.blogmultiplatform.util

// JVM-specific extension functions for automatic file/function logging

fun Logger.logInfo(message: String) {
    val stack = Throwable().stackTrace[1]
    info(message, stack.fileName ?: "", stack.methodName ?: "")
}
fun Logger.logWarning(message: String) {
    val stack = Throwable().stackTrace[1]
    warning(message, stack.fileName ?: "", stack.methodName ?: "")
}
fun Logger.logError(message: String) {
    val stack = Throwable().stackTrace[1]
    error(message, stack.fileName ?: "", stack.methodName ?: "")
}

