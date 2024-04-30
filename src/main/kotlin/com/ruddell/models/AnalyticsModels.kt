package com.ruddell.models

import com.ruddell.extensions.toMySqlString
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AnalyticsItem(
    val event: String,
    val query: String?,
    val extraData: String? = null,
    val id: String? = null,
    val dateCreated: String = Date().toMySqlString()
)

enum class AnalyticEvent(val key: String) {
    HOME_PAGE_LOAD("home_page_load"),
    SEARCH("search"),
    GET_FEED("get_feed"),
    GET_TRANSCRIPT("get_transcript"),
    VIEW_CHANNEL_VIDEOS("view_channel_videos")
}
