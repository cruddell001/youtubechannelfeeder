package com.ruddell.repository.database

import com.ruddell.extensions.log
import com.ruddell.models.Transcript
import com.ruddell.models.TranscriptText
import java.sql.ResultSet

/*
@Serializable
data class TranscriptText(
    val start: Double,
    val duration: Double,
    val content: String
)

@Serializable
data class Transcript(
    val videoId: String,
    val texts: List<TranscriptText>
)
 */
class TranscriptHelper: DatabaseHelper<Transcript>() {
    override fun mapRowToModel(rs: ResultSet): Transcript {
        return Transcript(
            videoId = rs.getString("video_id"),
            texts = emptyList()
        )
    }

    override fun insert(model: Transcript): Boolean {
        delete(model.videoId)
        getConnection { connection ->
            connection.prepareStatement(
                """
                INSERT INTO transcripts (video_id)
                VALUES (?)
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, model.videoId)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun read(id: String): Transcript? {
        log("TranscriptHelper.read($id)")
        val transcript = getConnection { connection ->
            connection.prepareStatement(
                """
                SELECT * FROM transcripts WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, id)
                statement.executeQuery().use { rs ->
                    if (rs.next()) {
                        return@getConnection mapRowToModel(rs)
                    } else {
                        log("TranscriptHelper.read($id) - no results found")
                        return@getConnection null
                    }
                }
            }
        }
        return transcript
    }

    override fun getAll(): List<Transcript> {
        val results = mutableListOf<Transcript>()
        getConnection { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT * FROM transcripts").use { rs ->
                    while (rs.next()) {
                        results.add(mapRowToModel(rs))
                    }
                }
            }
        }
        return results
    }

    override fun update(model: Transcript): Boolean {
        getConnection { connection ->
            connection.prepareStatement(
                """
                UPDATE transcripts
                SET video_id = ?
                WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, model.videoId)
                statement.setString(2, model.videoId)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun delete(id: String): Boolean {
        getConnection { connection ->
            connection.prepareStatement(
                """
                DELETE FROM transcripts WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, id)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun createTable() {
        getConnection { connection ->
            connection.createStatement().use { statement ->
                // create transcripts table with id and video_id columns, and an index on both
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS transcripts (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        video_id VARCHAR(255) NOT NULL,
                        INDEX video_id_index (video_id),
                        INDEX id_index (id)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}

class TranscriptTextHelper: DatabaseHelper<TranscriptText>() {
    override fun mapRowToModel(rs: ResultSet): TranscriptText {
        return TranscriptText(
            videoId = rs.getString("video_id"),
            start = rs.getDouble("start"),
            duration = rs.getDouble("duration"),
            content = rs.getString("content")
        )
    }

    override fun insert(model: TranscriptText): Boolean {
        getConnection { connection ->
            connection.prepareStatement(
                """
                INSERT INTO transcript_texts (video_id, start, duration, content)
                VALUES (?, ?, ?, ?)
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, model.videoId)
                statement.setDouble(2, model.start)
                statement.setDouble(3, model.duration)
                statement.setString(4, model.content)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun read(id: String): TranscriptText? {
        getConnection { connection ->
            connection.prepareStatement(
                """
                SELECT * FROM transcript_texts WHERE id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, id)
                statement.executeQuery().use { rs ->
                    if (rs.next()) {
                        return@getConnection mapRowToModel(rs)
                    }
                }
            }
        }
        return null
    }

    fun getByVideoId(videoId: String): List<TranscriptText> {
        val results = mutableListOf<TranscriptText>()
        getConnection { connection ->
            connection.prepareStatement(
                """
                SELECT * FROM transcript_texts WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, videoId)
                statement.executeQuery().use { rs ->
                    while (rs.next()) {
                        results.add(mapRowToModel(rs))
                    }
                }
            }
        }
        return results
    }

    override fun getAll(): List<TranscriptText> {
        val results = mutableListOf<TranscriptText>()
        getConnection { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT * FROM transcript_texts").use { rs ->
                    while (rs.next()) {
                        results.add(mapRowToModel(rs))
                    }
                }
            }
        }
        return results
    }

    override fun update(model: TranscriptText): Boolean {
        getConnection { connection ->
            connection.prepareStatement(
                """
                UPDATE transcript_texts
                SET start = ?, duration = ?, content = ?
                WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setDouble(1, model.start)
                statement.setDouble(2, model.duration)
                statement.setString(3, model.content)
                statement.setString(4, model.videoId)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun delete(id: String): Boolean {
        getConnection { connection ->
            connection.prepareStatement(
                """
                DELETE FROM transcript_texts WHERE video_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, id)
                statement.executeUpdate()
            }
        }
        return true
    }

    override fun createTable() {
        getConnection { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS transcript_texts (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        video_id VARCHAR(255) NOT NULL,
                        start DOUBLE NOT NULL,
                        duration DOUBLE NOT NULL,
                        content TEXT NOT NULL,
                        INDEX video_id_index (video_id),
                        INDEX id_index (id)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
