package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.Newsletter
import com.example.blogmultiplatform.util.LoggerImpl
import com.example.blogmultiplatform.util.logError
import com.example.blogmultiplatform.util.logInfo
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue

@Api(routeOverride = "subscribe")
suspend fun subscribeNewsletter(context: ApiContext) {
    val logger = LoggerImpl // Use singleton
    try {
        val newsletter = context.req.getBody<Newsletter>()
        context.res.setBody(newsletter?.let {
            context.data.getValue<MongoDB>().subscribe(newsletter = it)
        })
        logger.logInfo("Newsletter subscription request processed.")
    } catch (e: Exception) {
        logger.logError("Newsletter subscription error: ${e.message}")
        context.res.setBody(e.message)
    }
}