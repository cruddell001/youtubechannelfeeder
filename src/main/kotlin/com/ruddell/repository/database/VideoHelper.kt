package com.ruddell.repository.database

import com.ruddell.models.YoutubeItem
import java.sql.ResultSet

/*
data class YoutubeItem(
    val id: String?,
    val thumbnailUrl: String?,
    val title: String?,
    val subtitle: String?,
    val author: String?,
    val description: String?,
    val channelId: String?,
    val lastUpdated: String = Date().toMySqlString()
)
 */
class VideoHelper: DatabaseHelper<YoutubeItem>() {
    override fun mapRowToModel(rs: ResultSet): YoutubeItem = YoutubeItem(
        id = rs.getString("id"),
        thumbnailUrl = rs.getString("thumbnailUrl"),
        title = rs.getString("title"),
        subtitle = rs.getString("subtitle"),
        author = rs.getString("author"),
        description = rs.getString("description"),
        channelId = rs.getString("channelId"),
        lastUpdated = rs.getString("lastUpdated"),
        youtubeDate = rs.getString("youtubeDate"),
    )

    override fun insert(model: YoutubeItem): Boolean {
        delete(model.id ?: "")
        return getConnection { connection ->
            val sql = "INSERT INTO youtube_videos (id, thumbnailUrl, title, subtitle, author, description, channelId, lastUpdated, youtubeDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.id)
                stmt.setString(2, model.thumbnailUrl)
                stmt.setString(3, model.title)
                stmt.setString(4, model.subtitle)
                stmt.setString(5, model.author)
                stmt.setString(6, model.description)
                stmt.setString(7, model.channelId)
                stmt.setString(8, model.lastUpdated)
                stmt.setString(9, model.youtubeDate)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun read(id: String): YoutubeItem? {
        val videoId = id
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_videos WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, videoId)
                val rs = stmt.executeQuery()
                if (rs != null && rs.next()) {
                    mapRowToModel(rs)
                } else null
            }
        }
    }

    override fun getAll(): List<YoutubeItem> {
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_videos"
            connection.prepareStatement(sql)?.use { stmt ->
                val rs = stmt.executeQuery()
                val list = mutableListOf<YoutubeItem>()
                while (rs != null && rs.next()) {
                    list.add(mapRowToModel(rs))
                }
                list
            } ?: emptyList()
        }
    }

    fun getByChannelId(channelId: String): List<YoutubeItem> {
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_videos WHERE channelId = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, channelId)
                val rs = stmt.executeQuery()
                val list = mutableListOf<YoutubeItem>()
                while (rs != null && rs.next()) {
                    list.add(mapRowToModel(rs))
                }
                list
            } ?: emptyList()
        }
    }

    override fun update(model: YoutubeItem): Boolean {
        return getConnection { connection ->
            val sql = "UPDATE youtube_videos SET thumbnailUrl = ?, title = ?, subtitle = ?, author = ?, description = ?, channelId = ?, lastUpdated = ? WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.thumbnailUrl)
                stmt.setString(2, model.title)
                stmt.setString(3, model.subtitle)
                stmt.setString(4, model.author)
                stmt.setString(5, model.description)
                stmt.setString(6, model.channelId)
                stmt.setString(7, model.lastUpdated)
                stmt.setString(8, model.id)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun delete(id: String): Boolean {
        return getConnection { connection ->
            val sql = "DELETE FROM youtube_videos WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, id)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun createTable() {
        getConnection { connection ->
            val sql = """
                CREATE TABLE IF NOT EXISTS youtube_videos (
                    id VARCHAR(255) PRIMARY KEY,
                    thumbnailUrl VARCHAR(255),
                    title VARCHAR(255),
                    subtitle VARCHAR(255),
                    author VARCHAR(255),
                    description TEXT,
                    channelId VARCHAR(255),
                    lastUpdated VARCHAR(255),
                    youtubeDate VARCHAR(255)
                )
            """.trimIndent()
            connection.prepareStatement(sql).execute()
        }
    }
}