package com.ruddell.plugins

import com.ruddell.extensions.log
import com.ruddell.extensions.sanitize
import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeItem
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import java.io.StringWriter

var templateConfig: Configuration? = null
fun Application.configureHtmlTemplating() {
    install(FreeMarker) {
        templateConfig = this
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}

fun renderYouTubeVideoForRss(channel: YoutubeChannel, item: YoutubeItem, freemarkerConfig: Configuration): String {
    val template = freemarkerConfig.getTemplate("yt_video_rss.ftl")
    return StringWriter().use { writer ->
        template.process(mapOf("video" to item.sanitize(), "channel" to channel), writer)
        writer.toString()
    } + "\n"
}

fun YoutubeItem.sanitize(): YoutubeItem = this.copy(
    description = description?.sanitizeForRss() ?: "",
    title = title?.sanitizeForRss() ?: "",
)

fun String.sanitizeForRss(): String = this
    .replace("&", "&amp;")  // Must be first to avoid replacing newly inserted & from other entities
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&apos;")

fun renderYouTubeRssFeed(channel: YoutubeChannel, videos: List<YoutubeItem>, freemarkerConfig: Configuration): String {
    val sanitized = channel.sanitize()
    log("renderYouTubeRssFeed: ${sanitized.title}: ${videos.size} videos")
    val itemsContent = videos.joinToString("") { renderYouTubeVideoForRss(sanitized, it, freemarkerConfig) }
    val mainTemplate = freemarkerConfig.getTemplate("yt_channel_rss.ftl")
    return StringWriter().use { writer ->
        mainTemplate.process(mapOf("channel" to sanitized, "videos" to itemsContent), writer)
        writer.toString()
    }.removePrefix("\n")
}

