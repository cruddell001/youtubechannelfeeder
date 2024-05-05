package com.ruddell.repository

import com.ruddell.BuildConfig
import com.ruddell.extensions.Log
import com.ruddell.extensions.decode
import com.ruddell.extensions.toJson
import com.ruddell.models.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object OpenAiHelper {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        // timeout
        install(HttpTimeout) {
            connectTimeoutMillis = 240_000 // 240 seconds
            socketTimeoutMillis = 240_000 // 240 seconds
            requestTimeoutMillis = 240_000 // 240 seconds
        }
    }

    suspend fun requestSummary(transcript: Transcript): String {
        val text = transcript.texts.joinToString("\n") { it.content }
        val prompt = "Summarize the following transcript, providing the salient points as bullet points.  Output in html format.\n\n$text"
        val request = ChatGptRequest(listOf(ChatGptMessage(prompt)))
        val response = makeOpenAiRequest(BuildConfig.OPENAI_API_KEY, request)
        Log.debug("requestSummary: $response")
        return response
    }

    private suspend fun makeOpenAiRequestWithFullResponse(apiKey: String, request: ChatGptRequest): ChatGptResponse {
        Log.debug("makeOpenAiRequest:")
        Log.debug(request.toJson())
        val apiKeyHeader = "Bearer $apiKey"

        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("https://api.openai.com/v1/chat/completions") {
                header("Authorization", apiKeyHeader)
                header("Content-Type", "application/json")
                setBody(request)
            }
        }

        val rawResponse = response.bodyAsText()
        Log.debug("makeOpenAiRequest: ${request.messages.first().content}")
        Log.debug(rawResponse)
        val openAIResponse: ChatGptResponse? = rawResponse.decode() ?: let {
            Log.debug("Failed to decode response")
            null
        }
        openAIResponse?.error?.let {
            Log.debug("ERROR: ${it.message}")
            if (it.shouldRetry()) {
                Log.debug("Retrying request after 5 seconds...")
                delay(5000)
                return makeOpenAiRequestWithFullResponse(apiKey, request)
            }
            return ChatGptResponse(error = OpenAiError(message = it.message))
        }
        return openAIResponse ?: ChatGptResponse(error = OpenAiError(message = "Invalid response"))
    }

    private fun OpenAiError.shouldRetry(): Boolean = this.message.contains("overloaded") || this.message.contains("retry") || this.type == "server_error"

    private suspend fun makeOpenAiRequest(apiKey: String, request: ChatGptRequest): String {
        val openAiResponse = makeOpenAiRequestWithFullResponse(apiKey, request)
        val cost = openAiResponse.estimateCost()
        Log.debug("makeOpenAiRequest - found ${openAiResponse.choices?.size} choices at a cost of $cost cents")
        return openAiResponse.choices?.first()?.message?.content ?: ""
    }

    /**
     * Estimate ChatGPT cost for 4 turbo model
     * Input cost = $10.00 / 1M tokens
     * Output cost = $30.00 / 1M tokens
     * return: cost in cents
     */
    private fun ChatGptResponse.estimateCost(): Double {
        val usage = usage ?: return 0.0
        val inputTokens = usage.prompt_tokens
        val outputTokens = usage.completion_tokens
        val inputCost = inputTokens / 1_000_000.0 * 10.0 * 100
        val outputCost = outputTokens / 1_000_000.0 * 30.0 * 100
        return inputCost + outputCost
    }
}

fun ChatGptResponse.contents(): String {
    return choices?.first()?.message?.content ?: ""
}
