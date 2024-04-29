package com.ruddell.models

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptText(
    val videoId: String,
    val start: Double,
    val duration: Double,
    val content: String
)

@Serializable
data class Transcript(
    val videoId: String,
    val texts: List<TranscriptText>
)
