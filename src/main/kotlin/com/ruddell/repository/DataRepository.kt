package com.ruddell.repository

import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeChannelSearch
import com.ruddell.repository.database.AppDatabase
import java.util.*

object DataRepository {

    fun performSearch(channelName: String): List<YoutubeChannel> {
        val previousQuery = AppDatabase.searchHelper.getAll().firstOrNull { it.query == channelName }
        if (previousQuery != null && !previousQuery.dateLastRan.isOlderThan(1)) {
            return AppDatabase.channelHelper.getAll().filter { previousQuery.channelIds.contains(it.channelId) }
        }
        val channels = YoutubeApi.searchChannels(channelName)
        AppDatabase.searchHelper.insert(YoutubeChannelSearch(channelName, channels.mapNotNull { it.channelId }))
        channels.forEach { AppDatabase.channelHelper.insert(it) }
        return channels
    }

    private fun Date?.isOlderThan(days: Int): Boolean {
        if (this == null) return true
        val now = Date()
        val diff = now.time - this.time
        val diffDays = diff / (24 * 60 * 60 * 1000)
        return diffDays > days
    }

}
