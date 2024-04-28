package com.ruddell.models

import com.ruddell.extensions.toDate
import com.ruddell.extensions.toMySqlString
import kotlinx.serialization.Serializable
import java.util.*

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
    val subscribers: Int? = null,
    val youtubeUrl: String = "",
    val lastUpdated: String = Date().toMySqlString()
) {
    val rssFeed: String get() = "https://www.youtubefeeds.com/feeds/$channelId"
    val dateLastUpdated: Date? get() = lastUpdated.toDate()
}

@Serializable
data class YoutubeChannelSearch(
    val query: String,
    val channelIds: List<String> = emptyList(),
    val lastRan: String = Date().toMySqlString()
) {
    val dateLastRan: Date? get() = lastRan.toDate()
}
