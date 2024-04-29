package com.ruddell.models

import com.ruddell.BuildConfig
import com.ruddell.extensions.toDate
import com.ruddell.extensions.toMySqlString
import com.ruddell.extensions.toRssString
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class YoutubeItem(
    val id: String?,
    val thumbnailUrl: String?,
    val title: String?,
    val subtitle: String?,
    val author: String?,
    val description: String?,
    val channelId: String?,
    val lastUpdated: String = Date().toMySqlString()
) {
    val dateLastUpdated: Date? get() = lastUpdated.toDate()
    val rssDateUpdated: String get() = dateLastUpdated?.toRssString() ?: ""
    val pageUrl: String get() = "${BuildConfig.BASE_URL}/video/$id"
}

@Serializable
data class YoutubeChannel(
    val channelId: String?,
    val thumbnailUrl: String?,
    val channelTitle: String?,
    val description: String?,
    val title: String?,
    val subscribers: Int? = null,
    val youtubeUrl: String = "",
    val lastUpdated: String = Date().toMySqlString(),
    val rssFeed: String = "${BuildConfig.BASE_URL}/rss/$channelId"
) {
    val dateLastUpdated: Date? get() = lastUpdated.toDate()
    val rssDateUpdated: String get() = dateLastUpdated?.toRssString() ?: ""
}

@Serializable
data class YoutubeChannelSearch(
    val query: String,
    val channelIds: List<String> = emptyList(),
    val lastRan: String = Date().toMySqlString()
) {
    val dateLastRan: Date? get() = lastRan.toDate()
}
