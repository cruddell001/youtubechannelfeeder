package com.ruddell.extensions

import com.google.api.services.youtube.model.Channel
import com.google.api.services.youtube.model.SearchResult
import com.ruddell.models.Transcript
import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeItem
import com.ruddell.plugins.sanitizeForRss

fun SearchResult.toItem() = YoutubeItem(
    id = this.id?.videoId,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    title = this.snippet?.title,
    subtitle = this.snippet?.channelTitle,
    author = this.snippet?.channelTitle,
    channelId = this.snippet?.channelId,
    description = this.snippet?.description ?: "",
    youtubeDate = this.snippet?.publishedAt?.toDate()?.toRfc822()
)

fun SearchResult.toChannel() = YoutubeChannel(
    channelId = this.id?.channelId,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    channelTitle = this.snippet?.title,
    description = this.snippet?.description,
    title = this.snippet?.title
)

fun Channel.toChannel() = YoutubeChannel(
    channelId = this.id,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    channelTitle = this.snippet?.title,
    description = this.snippet?.description,
    title = this.snippet?.title
)

fun YoutubeChannel.sanitize(): YoutubeChannel = this.copy(
    title = title?.sanitizeForRss() ?: channelTitle?.sanitizeForRss() ?: "",
    description = description?.sanitizeForRss() ?: ""
)

fun Transcript.hasSummary(): Boolean = this.texts.any { it.start < 0.0 }
