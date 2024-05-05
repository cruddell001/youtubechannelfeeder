package com.ruddell.repository

import com.ruddell.extensions.toJson
import com.ruddell.models.AnalyticEvent
import com.ruddell.repository.database.AppDatabase
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

object AnalyticsManager {

    fun trackHomePageLoad(call: ApplicationCall) {
        val extraData = call.buildWebRequest("/")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.HOME_PAGE_LOAD, "", extraData.toJson())
    }

    fun trackChannelSearch(call: ApplicationCall) {
        val extraData = call.buildWebRequest("/searchApi")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.SEARCH, call.request.queryParameters["q"] ?: "", extraData.toJson())
    }

    fun trackChannelFeed(call: ApplicationCall) {
        val channelId = call.parameters["channelId"] ?: ""
        val extraData = call.buildWebRequest("/rss/$channelId")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.GET_FEED, channelId, extraData.toJson())
    }

    fun trackVideoPageLoad(call: ApplicationCall) {
        val videoId = call.parameters["videoId"] ?: ""
        val extraData = call.buildWebRequest("/video/$videoId")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.LOAD_VIDEO_PAGE, videoId, extraData.toJson())
    }

    fun trackTranscriptRequest(call: ApplicationCall) {
        val videoId = call.parameters["videoId"] ?: ""
        val extraData = call.buildWebRequest("/video/$videoId")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.GET_TRANSCRIPT, videoId, extraData.toJson())
    }

    fun trackChannelVideosWebView(call: ApplicationCall) {
        val channelId = call.parameters["channelId"] ?: ""
        val extraData = call.buildWebRequest("/channel/$channelId/videos")
        AppDatabase.analyticsHelper.insert(AnalyticEvent.VIEW_CHANNEL_VIDEOS, channelId, extraData.toJson())
    }

    private fun ApplicationCall.buildWebRequest(path: String): WebRequest {
        val ipAddress = request.header("X-Forwarded-For") ?: request.local.remoteHost
        return WebRequest(
            path = path,
            ipAddress = ipAddress,
            userAgent = request.headers["User-Agent"] ?: ""
        )
    }
}

@Serializable
data class WebRequest(
    val path: String,
    val ipAddress: String,
    val userAgent: String
)
