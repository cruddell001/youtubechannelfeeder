package com.ruddell.repository


import com.ruddell.extensions.toDate
import com.ruddell.extensions.toMySqlString
import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeChannelSearch
import com.ruddell.models.YoutubeItem
import com.ruddell.repository.database.AppDatabase
import java.util.*

object DataRepository {

    fun performSearch(channelName: String): List<YoutubeChannel> {
        val previousQuery = AppDatabase.searchHelper.getAll().firstOrNull { it.query == channelName }
        if (previousQuery != null && !previousQuery.dateLastRan.isOlderThan(1f)) {
            return AppDatabase.channelHelper.getAll().filter { previousQuery.channelIds.contains(it.channelId) }
        }
        val channels = YoutubeApi.searchChannels(channelName)
        AppDatabase.searchHelper.insert(YoutubeChannelSearch(channelName, channels.mapNotNull { it.channelId }))
        channels.forEach { AppDatabase.channelHelper.insert(it) }
        return channels
    }

    fun getChannel(channelId: String): YoutubeChannel? {
        return AppDatabase.channelHelper.read(channelId)
    }

    fun getVideos(channelId: String): List<YoutubeItem> {
        val channel = getChannel(channelId)
        if (channel.isStale()) {
            println("getVideos($channelId) is stale - fetching list from api")
            val videoList = YoutubeApi.getVideosForChannel(channelId)
            println("found ${videoList.size} videos for channel $channelId")
            channel?.copy(lastUpdated = Date().toMySqlString())?.let { updated ->
                AppDatabase.channelHelper.insert(updated)
            }
            videoList.forEach {
                AppDatabase.videoHelper.insert(it)
            }
            return videoList
        }
        println("getVideos($channelId) is fresh - fetching list from db")
        return AppDatabase.videoHelper.getByChannelId(channelId)
    }

    private fun YoutubeChannel?.isStale(): Boolean {
        if (this == null) return true
        val timeElapsed = this.dateLastRan()?.timeElapsed() ?: 1f
        val maxDurationInDays = 1f / 24f
        println("channel is stale?: $timeElapsed > $maxDurationInDays (from ${this.dateLastRan()}): ${this.lastUpdated}")
        return timeElapsed > maxDurationInDays
    }

    private fun Date?.isOlderThan(days: Float): Boolean {
        if (this == null) return true
        val now = Date()
        val diff = (now.time - this.time).toFloat()
        val diffDays = diff / (24 * 60 * 60 * 1000)
        return diffDays > days
    }

    private fun YoutubeChannel?.dateLastRan(): Date? {
        return this?.lastUpdated?.toDate()
    }

    private fun Date.timeElapsed(): Float {
        val now = Date()
        val diff = (now.time - this.time).toFloat()
        return diff / (24 * 60 * 60 * 1000)
    }

}
