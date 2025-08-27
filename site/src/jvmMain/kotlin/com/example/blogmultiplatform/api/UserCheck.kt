package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.example.blogmultiplatform.util.LoggerImpl
import com.example.blogmultiplatform.util.logError
import com.example.blogmultiplatform.util.logInfo
import com.example.blogmultiplatform.util.logWarning
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable

@Api(routeOverride = "usercheck")
suspend fun userCheck(context: ApiContext) {
    val logger = LoggerImpl // Use singleton
    logger.logInfo("UserCheck.kt::userCheck - userCheck called")
    try {
        logger.logInfo("UserCheck.kt::userCheck - Decoding user request")
        val userRequest =
            context.req.body?.decodeToString()?.let { Json.decodeFromString<User>(it) }
        logger.logInfo("UserCheck.kt::userCheck - Decoded user request: ${userRequest?.username}")
        logger.logInfo("UserCheck.kt::userCheck - Calling checkUserExistence")
        val user = userRequest?.let {
            context.data.getValue<MongoDB>().checkUserExistence(
                User(username = it.username, password = it.password, role = it.role)
            )
        }
        logger.logInfo("UserCheck.kt::userCheck - checkUserExistence result: ${user?.username}")
        if (user != null) {
            logger.logInfo("UserCheck.kt::userCheck - Preparing success response for user: ${user.username}")
            context.res.setBodyText(
                Json.encodeToString(
                    UserWithoutPassword(_id = user._id, username = user.username, role = user.role)
                )
            )
            logger.logInfo("UserCheck.kt::userCheck - User check successful for user: ${user.username}")
        } else {
            logger.logInfo("UserCheck.kt::userCheck - Preparing failure response: User doesn't exist.")
            context.res.setBodyText(Json.encodeToString(ErrorResponse("User doesn't exist.")))
            logger.logWarning("UserCheck.kt::userCheck - User check failed: User doesn't exist.")
        }
        logger.logInfo("UserCheck.kt::userCheck - userCheck completed")
    } catch (e: Exception) {
        logger.logError("UserCheck.kt::userCheck - User check error: ${e.message}")
        context.res.setBodyText(Json.encodeToString(ErrorResponse(e.message ?: "Unknown error")))
    }
}

@Api(routeOverride = "checkuserid")
suspend fun checkUserId(context: ApiContext) {
    val logger = LoggerImpl // Use singleton
    try {
        val idRequest =
            context.req.body?.decodeToString()?.let { Json.decodeFromString<String>(it) }
        val result = idRequest?.let {
            logger.logInfo("Check user ID for : $it")
            context.data.getValue<MongoDB>().checkUserId(it)
        }
        if (result != null) {
            context.res.setBodyText(Json.encodeToString(result))
            logger.logInfo("Check user ID result: $result for ID: $idRequest")
        } else {
            context.res.setBodyText(Json.encodeToString(false))
            logger.logWarning("Check user ID failed: null result for ID: $idRequest")
        }
    } catch (e: Exception) {
        logger.logError("Check user ID error: ${e.message}")
        context.res.setBodyText(Json.encodeToString(false))
    }
}

@Serializable
data class ErrorResponse(val error: String)
