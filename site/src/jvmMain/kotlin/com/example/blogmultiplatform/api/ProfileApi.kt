package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.Profile
import com.example.blogmultiplatform.util.LoggerImpl
import com.example.blogmultiplatform.util.logError
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(val success: Boolean, val message: String, val profile: Profile? = null)

@Api(routeOverride = "getprofile")
suspend fun getProfile(context: ApiContext) {
    val logger = LoggerImpl
    try {
        val userId = context.req.body?.decodeToString()?.let { Json.decodeFromString<String>(it) }
        val mongo = context.data.getValue<MongoDB>()
        val profile = userId?.let { mongo.getProfile(it) }
        if (profile != null) {
            context.res.setBodyText(Json.encodeToString(ProfileResponse(true, "Profile found", profile)))
        } else {
            context.res.setBodyText(Json.encodeToString(ProfileResponse(false, "Profile not found")))
        }
    } catch (e: Exception) {
        LoggerImpl.logError("getProfile error: ${e.message}")
        context.res.setBodyText(Json.encodeToString(ProfileResponse(false, e.message ?: "Unknown error")))
    }
}

@Api(routeOverride = "saveprofile")
suspend fun saveProfile(context: ApiContext) {
    val logger = LoggerImpl
    try {
        val profile = context.req.body?.decodeToString()?.let { Json.decodeFromString<Profile>(it) }
        val mongo = context.data.getValue<MongoDB>()
        val success = profile?.let { mongo.saveProfile(it) } ?: false
        if (success) {
            context.res.setBodyText(Json.encodeToString(ProfileResponse(true, "Profile saved", profile)))
        } else {
            context.res.setBodyText(Json.encodeToString(ProfileResponse(false, "Failed to save profile")))
        }
    } catch (e: Exception) {
        LoggerImpl.logError("saveProfile error: ${e.message}")
        context.res.setBodyText(Json.encodeToString(ProfileResponse(false, e.message ?: "Unknown error")))
    }
}
