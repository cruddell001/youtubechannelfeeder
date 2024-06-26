package com.ruddell.plugins

import com.ruddell.BuildConfig
import com.ruddell.extensions.*
import com.ruddell.models.AnalyticEvent
import com.ruddell.models.YoutubeChannel
import com.ruddell.repository.AnalyticsManager
import com.ruddell.repository.DataRepository
import com.ruddell.repository.YoutubeApi
import com.ruddell.repository.database.AppDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/searchTest") {
            val query = call.request.queryParameters["q"] ?: ""
            val res: String = YoutubeApi.searchTest(query)
            // add json as content type
            call.response.headers.append("Content-Type", "application/json")
            call.respondText(res)
        }
        staticResources("/images", "images")
        get("/") {
            AnalyticsManager.trackHomePageLoad(call)
            call.respond(FreeMarkerContent("channels.ftl", mapOf("results" to emptyList<YoutubeChannel>(), "baseUrl" to BuildConfig.BASE_URL)))
        }
        get("/rss/{channelId}") {
            AnalyticsManager.trackChannelFeed(call)
            val channelId = call.parameters["channelId"] ?: ""
            log("getting rss feed for channel: $channelId")
            val channel = DataRepository.getChannel(channelId)?.let {
                val dateUpdated = it.lastUpdated.toDate()
                val rssDate = dateUpdated?.toRfc822()
                it.copy(rssLastUpdated = rssDate ?: "")
            }
            val videos = DataRepository.getVideos(channelId)
            val freemarkerConfig = templateConfig
            if (channel == null || videos == null || freemarkerConfig == null) {
                call.respondText("Channel not found")
                return@get
            }
            // header for rss feed:
            call.response.headers.append("Content-Type", "text/xml;charset=UTF-8")
            log("showing rss feed for channel: ${channel.channelTitle}: ${videos.size} videos")
            val body = renderYouTubeRssFeed(channel, videos, freemarkerConfig)
            call.respondText(body)
        }
        get("/searchApi") {
            AnalyticsManager.trackChannelSearch(call)
            val query = call.request.queryParameters["q"]
            if (query == null) {
                call.respondText("[]")
            } else {
                val res = DataRepository.performSearch(query)
                call.respond(res)
            }
        }
        get("/video/{videoId}") {
            AnalyticsManager.trackVideoPageLoad(call)
            val videoId = call.parameters["videoId"] ?: ""
            val video = DataRepository.getVideo(videoId)
            if (video == null) {
                call.respondText("Video not found")
                return@get
            }

            call.respond(FreeMarkerContent("video.ftl", mapOf("youtubeItem" to video)))
        }
        get("cache/transcription/{videoId}") {
            val videoId = call.parameters["videoId"] ?: ""
            val transcript = DataRepository.getCachedTranscription(videoId)?.takeIf { it.texts.isNotEmpty() && it.hasSummary() }
            if (transcript == null) {
                call.respond(HttpStatusCode.NoContent, "")
                return@get
            }
            call.respond(transcript)
        }
        get("transcribe/{videoId}") {
            AnalyticsManager.trackTranscriptRequest(call)
            val videoId = call.parameters["videoId"] ?: ""
            val transcript = DataRepository.transcribeVideo(videoId)
            if (transcript == null) {
                call.respondText("Unable to transcribe video")
                return@get
            }
            call.respond(transcript)
        }
        get("/channel/{channelId}/videos") {
            AnalyticsManager.trackChannelVideosWebView(call)
            val channelId = call.parameters["channelId"]
            if (channelId.isNullOrEmpty()) {
                call.respondText("Channel not found")
                return@get
            }
            val channel = DataRepository.getChannel(channelId)?.let {
                val dateUpdated = it.lastUpdated.toDate()
                val rssDate = dateUpdated?.toRfc822()
                it.copy(rssLastUpdated = rssDate ?: "").sanitize()
            } ?: YoutubeChannel("", "", "", "", "")
            val videos = DataRepository.getVideos(channelId)
            call.respond(FreeMarkerContent("yt_channel_listing.ftl", mapOf("videos" to videos, "channel" to channel)))
        }
    }
}
