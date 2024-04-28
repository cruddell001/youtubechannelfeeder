package com.ruddell.plugins

import com.ruddell.models.YoutubeItem
import com.ruddell.repository.YoutubeApi
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/search") {
            val res = YoutubeApi.searchChannels("two minute papers")
            call.respond(res)
        }
    }
}
