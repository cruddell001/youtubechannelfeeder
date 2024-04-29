package com.ruddell.plugins

import com.ruddell.models.YoutubeChannel
import com.ruddell.repository.DataRepository
import com.ruddell.repository.YoutubeApi
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
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
        get("/") {
            call.respond(FreeMarkerContent("channels.ftl", mapOf("results" to emptyList<YoutubeChannel>())))
        }
        get("/rss/{channelId}") {
            val channelId = call.parameters["channelId"] ?: ""
            val channel = DataRepository.getChannel(channelId)
            val videos = DataRepository.getVideos(channelId)
            val freemarkerConfig = templateConfig
            if (channel == null || videos == null || freemarkerConfig == null) {
                call.respondText("Channel not found")
                return@get
            }
            // header for rss feed:
            call.response.headers.append("Content-Type", "text/xml;charset=UTF-8")
            println("showing rss feed for channel: ${channel.channelTitle}: ${videos.size} videos")
            val body = renderYouTubeRssFeed(channel, videos, freemarkerConfig)
            call.respondText(body)
        }
        get("/searchApi") {
            val query = call.request.queryParameters["q"]
            if (query == null) {
                call.respondText("[]")
            } else {
                val res = DataRepository.performSearch(query)
                call.respond(res)
            }
        }
    }
}
