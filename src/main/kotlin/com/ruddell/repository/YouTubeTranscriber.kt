package com.ruddell.repository

import com.ruddell.models.Transcript
import com.ruddell.models.TranscriptText
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

object YouTubeTranscriber {

    suspend fun transcribeVideo(videoId: String): Transcript? {
        val url = "https://youtubetranscript.com/?server_vid2=$videoId"
        val res = client.get(url) {}
        val body = res.bodyAsText()

        return parseTranscript(body)
    }

    private val client = HttpClient(CIO) {}

    private fun parseTranscript(xmlContent: String): Transcript? {
        try {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(xmlContent.byteInputStream())
            doc.documentElement.normalize()

            val nodeList = doc.getElementsByTagName("text")
            val texts = mutableListOf<TranscriptText>()

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val element = node as Element
                    val start = element.getAttribute("start").toDouble()
                    val duration = element.getAttribute("dur").toDouble()
                    val content = element.textContent.trim()

                    texts.add(TranscriptText(start, duration, content))
                }
            }

            return Transcript(texts)
        } catch (e: Throwable) {
            println("Error parsing transcript: $e")
            e.printStackTrace()
            return null
        }

    }
}
