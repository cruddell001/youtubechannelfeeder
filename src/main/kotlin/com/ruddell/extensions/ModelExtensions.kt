package com.ruddell.extensions

import com.google.api.services.youtube.model.SearchResult
import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeItem

fun SearchResult.toItem() = YoutubeItem(
    id = this.id?.videoId,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    title = this.snippet?.title,
    subtitle = this.snippet?.channelTitle,
    author = this.snippet?.channelTitle
)

fun SearchResult.toChannel() = YoutubeChannel(
    channelId = this.id?.channelId,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    channelTitle = this.snippet?.title,
    description = this.snippet?.description,
    title = this.snippet?.title
)