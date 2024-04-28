package com.ruddell.plugins

import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeItem
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
            val res: String = YoutubeApi.searchChannelsTest(query)
            // add json as content type
            call.response.headers.append("Content-Type", "application/json")
            call.respondText(res)
        }
        get("/") {
            call.respond(FreeMarkerContent("channels.ftl", mapOf("results" to emptyList<YoutubeChannel>())))
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
