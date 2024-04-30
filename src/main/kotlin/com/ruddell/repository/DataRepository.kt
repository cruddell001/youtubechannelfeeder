package com.ruddell.repository


import com.ruddell.extensions.log
import com.ruddell.extensions.toDate
import com.ruddell.extensions.toMySqlString
import com.ruddell.extensions.toRfc822
import com.ruddell.models.Transcript
import com.ruddell.models.YoutubeChannel
import com.ruddell.models.YoutubeChannelSearch
import com.ruddell.models.YoutubeItem
import com.ruddell.repository.database.AppDatabase
import kotlinx.coroutines.runBlocking
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
        val cached = AppDatabase.channelHelper.read(channelId)
        if (cached == null) {
            val fresh = YoutubeApi.getChannelById(channelId).firstOrNull()
            if (fresh != null) {
                AppDatabase.channelHelper.insert(fresh)
                return fresh
            }
        }
        return cached
    }

    fun getVideos(channelId: String): List<YoutubeItem> {
        val channel = getChannel(channelId)
        val cachedVideos = AppDatabase.videoHelper.getByChannelId(channelId)
        if (channel.isStale() || cachedVideos.isEmpty()) {
            log("getVideos($channelId) is stale - fetching list from api")
            val videoList = YoutubeApi.getVideosForChannel(channelId).map { item ->
                val rssDate = item.lastUpdated.takeIf { it.isNotEmpty() } ?: item.lastUpdated.toDate()?.toRfc822() ?: ""
                item.copy(rssLastUpdated = rssDate)
            }
            log("found ${videoList.size} videos for channel $channelId")
            channel?.copy(lastUpdated = Date().toMySqlString())?.let { updated ->
                AppDatabase.channelHelper.insert(updated)
            }
            videoList.forEach {
                AppDatabase.videoHelper.insert(it)
            }
            return videoList
        }
        log("getVideos($channelId) is fresh - fetching list from db")
        return AppDatabase.videoHelper.getByChannelId(channelId)
    }

    fun getVideo(videoId: String): YoutubeItem? {
        return AppDatabase.videoHelper.read(videoId)
    }

    fun transcribeVideo(videoId: String): Transcript? = runBlocking {
        val cachedTranscript = AppDatabase.transcriptHelper
            .read(videoId)
            ?.copy(texts = AppDatabase.transcriptTextHelper.getByVideoId(videoId))
        if (cachedTranscript != null) {
            log("transcribeVideo($videoId) found cached transcript")
            return@runBlocking cachedTranscript
        }

        log("transcribeVideo($videoId) fetching fresh transcript")
        val freshTranscript = YouTubeTranscriber.transcribeVideo(videoId)
        if (freshTranscript != null) {
            AppDatabase.transcriptHelper.insert(freshTranscript)
            AppDatabase.transcriptTextHelper.delete(videoId)
            freshTranscript.texts.forEach {
                AppDatabase.transcriptTextHelper.insert(it.copy(videoId = videoId))
            }
        }

        return@runBlocking freshTranscript
    }

    private fun YoutubeChannel?.isStale(): Boolean {
        if (this == null) return true
        val timeElapsed = this.dateLastRan()?.timeElapsed() ?: 1f
        val maxDurationInDays = 3f / 24f
        log("channel is stale?: $timeElapsed > $maxDurationInDays (from ${this.dateLastRan()}): ${this.lastUpdated}")
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
