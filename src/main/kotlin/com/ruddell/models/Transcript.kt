package com.ruddell.models

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptText(
    val start: Double,
    val duration: Double,
    val content: String
)

@Serializable
data class Transcript(
    val texts: List<TranscriptText>
)
