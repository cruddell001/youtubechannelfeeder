package com.ruddell.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(val prompt: String, val max_tokens: Int, val model: String = "gpt-3.5-turbo")

@Serializable
data class ChatGptRequest(val messages: List<ChatGptMessage>, val model: String = "gpt-4-turbo-preview")

@Serializable
data class DalleRequest(val prompt: String)

@Serializable
data class DalleResponse(
    val created: Long,
    val data: List<DalleImage>? = null
)

@Serializable
data class DalleImage(
    val url: String? = null
)

@Serializable
data class ChatGptMessage(val content: String, val role: String = "user")

@Serializable
data class ChatGptResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<ChatGptChoice>? = null,
    val error: OpenAiError? = null,
    val usage: ChatGptUsage? = null
)

@Serializable
data class ChatGptChoice(
    val message: ChatGptMessage? = null
)

@Serializable
data class ChatGptUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class OpenAIResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<OpenAIChoice>? = null,
    val error: OpenAiError? = null
)

@Serializable
data class OpenAIChoice(val text: String)

@Serializable
data class OpenAiError(
    val message: String = "",
    val type: String = "",
    val param: String? = "",
    val code: Int? = null
)