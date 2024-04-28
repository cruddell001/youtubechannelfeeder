package com.ruddell.repository

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.ruddell.BuildConfig
import com.ruddell.extensions.toItem
import com.ruddell.models.YoutubeItem
import java.io.IOException
import java.security.GeneralSecurityException


class YoutubeApi {
    private var service: YouTube? = null

    init {
        service = getService()
    }

    companion object {
        private const val API_KEY = BuildConfig.YOUTUBE_API_KEY
        private val SCOPES: Collection<String> = listOf("https://www.googleapis.com/auth/youtube.readonly")

        private const val APPLICATION_NAME = "API code samples"
        private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    }

    fun search(query: String): List<YoutubeItem> {
        val request = service?.search()?.list("snippet")
        val response: SearchListResponse? = request
            ?.setQ(query)
            ?.setMaxResults(50)
            ?.setKey(API_KEY)
            ?.execute()

        println("search($query): found ${response?.items?.size} items with nextPage: ${response?.nextPageToken}")

        val additionalPage = response?.nextPageToken?.let { token ->
            request
                ?.setQ(query)
                ?.setMaxResults(50)
                ?.setPageToken(token)
                ?.setKey(API_KEY)
                ?.execute()
        }

        println("search($query): found ${additionalPage?.items?.size} additional items")

        return ((response?.items ?: listOf()) + (additionalPage?.items ?: listOf())).map { it.toItem() }
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


}
