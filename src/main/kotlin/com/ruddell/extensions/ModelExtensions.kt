package com.ruddell.extensions

import com.google.api.services.youtube.model.SearchResult
import com.ruddell.models.YoutubeItem

fun SearchResult.toItem() = YoutubeItem(
    id = this.id?.videoId,
    thumbnailUrl = this.snippet?.thumbnails?.medium?.url,
    title = this.snippet?.title,
    subtitle = this.snippet?.channelTitle,
    author = this.snippet?.channelTitle
)