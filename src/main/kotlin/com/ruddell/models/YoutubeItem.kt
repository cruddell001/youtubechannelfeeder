package com.ruddell.models

import kotlinx.serialization.Serializable

@Serializable
data class YoutubeItem(
    val id: String?,
    val thumbnailUrl: String?,
    val title: String?,
    val subtitle: String?,
    val author: String?,
)

@Serializable
data class YoutubeChannel(
    val channelId: String?,
    val thumbnailUrl: String?,
    val channelTitle: String?,
    val description: String?,
    val title: String?,
    val subscribers: Int? = null
)
