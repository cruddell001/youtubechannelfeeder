package com.ruddell.repository

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.gson.Gson
import com.ruddell.BuildConfig
import com.ruddell.extensions.toChannel
import com.ruddell.models.YoutubeChannel
import java.io.IOException
import java.security.GeneralSecurityException


object YoutubeApi {
    private var service: YouTube? = null

    init {
        service = getService()
    }

    private const val API_KEY = BuildConfig.YOUTUBE_API_KEY
    private val SCOPES: Collection<String> = listOf("https://www.googleapis.com/auth/youtube.readonly")

    private const val APPLICATION_NAME = "API code samples"
    private val JSON_FACTORY: JsonFactory get() = GsonFactory.getDefaultInstance()

    fun searchChannels(query: String): List<YoutubeChannel> {
        val service: YouTube = getService() ?: return emptyList()
        val request = service.search()?.list("snippet")
        val response: SearchListResponse = request
            ?.setQ(query)
            ?.setType("channel")
            ?.setPart("snippet")
            ?.setMaxResults(10)
            ?.setKey(API_KEY)
            ?.execute()
            ?: return emptyList()

        return response.items.map { it.toChannel() }.addDetails()
    }

    fun List<YoutubeChannel>.addDetails(): List<YoutubeChannel> {
        val channelIds = this.mapNotNull { it.channelId }
        val channelUrls = getChannelUrls(channelIds)
        val subscribers = getSubscribers(channelIds)

        return this.map { channel ->
            channel.copy(
                youtubeUrl = channelUrls[channel.channelId] ?: "",
                subscribers = subscribers[channel.channelId] ?: 0
            )
        }
    }

    fun getChannelUrls(channelIds: List<String>): Map<String, String> {
        val channelsRequest = service?.channels()?.list("snippet") ?: return emptyMap()
        val channelsResponse = channelsRequest
            .setId(channelIds.joinToString(","))  // Set multiple channel IDs
            .setKey(API_KEY)
            .execute()
            ?: return emptyMap()

        return channelsResponse.items.mapNotNull {
            it.id to it.snippet.customUrl
        }.toMap()
    }

    fun getSubscribers(channelIds: List<String>): Map<String, Int> {
        val channelsRequest = service?.channels()?.list("statistics") ?: return emptyMap()
        val channelsResponse = channelsRequest
            .setId(channelIds.joinToString(","))  // Set multiple channel IDs
            .setKey(API_KEY)
            .execute()
            ?: return emptyMap()

        return channelsResponse.items.mapNotNull {
            it.id to it.statistics.subscriberCount.toInt()
        }.toMap()
    }

    fun searchChannelsTest(query: String): String {
        val service: YouTube = getService() ?: return ""
        val request = service.search()?.list("snippet")
        val response: SearchListResponse = request
            ?.setQ(query)
            ?.setType("channel")
            ?.setPart("snippet")
            ?.setMaxResults(10)
            ?.setKey(API_KEY)
            ?.execute()
            ?: return ""

        return Gson().toJson(response)
    }

    fun getSubscribers(channelId: String): Int {
        val service: YouTube = getService() ?: return 0
        val request = service.channels().list("statistics")
        val response = request
            .setId(channelId)  // Set the channel ID
            .setKey(API_KEY)   // Set your API key
            .execute()         ?: return -1

        if (response.items.isEmpty()) {
            println("No channel found with ID: $channelId")
            return -1
        }

        val statistics = response.items[0].statistics
        val subscriberCount = statistics.subscriberCount.toInt()

        return subscriberCount
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    @Throws(GeneralSecurityException::class, IOException::class)
    private fun getService(): YouTube? {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val initializer = HttpRequestInitializer { }
        return YouTube.Builder(httpTransport, JSON_FACTORY, initializer)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun YoutubeChannel.addSubscribers(): YoutubeChannel {
        channelId ?: return this
        val subscribers = getSubscribers(channelId)
        return this.copy(subscribers = subscribers)
    }

}
