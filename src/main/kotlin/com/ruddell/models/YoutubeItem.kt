package com.ruddell.models

import com.ruddell.BuildConfig
import com.ruddell.extensions.toDate
import com.ruddell.extensions.toMySqlString
import com.ruddell.extensions.toRfc822
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
    val youtubeDate: String?,
    val lastUpdated: String = Date().toMySqlString(),
    val transcriptUrl: String = "${BuildConfig.BASE_URL}/video/$id",
    val rssLastUpdated: String = Date().toRfc822(),
    val publishedDate: String = youtubeDate?.takeIf { it.isNotEmpty() } ?: rssLastUpdated
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
    val lastUpdated: String = Date().toMySqlString(),
    val rssFeed: String = "${BuildConfig.BASE_URL}/rss/$channelId",
    val rssLastUpdated: String = ""
)

@Serializable
data class YoutubeChannelSearch(
    val query: String,
    val channelIds: List<String> = emptyList(),
    val lastRan: String = Date().toMySqlString()
) {
    val dateLastRan: Date? get() = lastRan.toDate()
}
