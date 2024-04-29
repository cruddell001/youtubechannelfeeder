package com.ruddell.repository.database

import com.ruddell.extensions.log
import com.ruddell.models.YoutubeChannel
import java.sql.ResultSet

class ChannelHelper: DatabaseHelper<YoutubeChannel>() {

    override fun insert(model: YoutubeChannel): Boolean {
        model.channelId?.let { delete(it) }
        log("inserting channel: ${model.channelId}: ${model.lastUpdated}")
        return getConnection { connection ->
            val sql = "INSERT INTO youtube_channels (channelId, thumbnailUrl, channelTitle, description, title, subscribers, youtubeUrl, lastUpdated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.channelId)
                stmt.setString(2, model.thumbnailUrl)
                stmt.setString(3, model.channelTitle)
                stmt.setString(4, model.description)
                stmt.setString(5, model.title)
                stmt.setInt(6, model.subscribers ?: 0)
                stmt.setString(7, model.youtubeUrl)
                stmt.setString(8, model.lastUpdated)
                stmt.executeUpdate() > 0
            } ?: false
        }

    }

    override fun getAll(): List<YoutubeChannel> {
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_channels"
            connection.prepareStatement(sql)?.use { stmt ->
                val rs = stmt.executeQuery()
                val list = mutableListOf<YoutubeChannel>()
                while (rs != null && rs.next()) {
                    list.add(mapRowToModel(rs))
                }
                list
            } ?: emptyList()
        }
    }

    override fun read(id: String): YoutubeChannel? {
        val channelId = id
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_channels WHERE channelId = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, channelId)
                val rs = stmt.executeQuery()
                if (rs != null && rs.next()) {
                    mapRowToModel(rs)
                } else null
            }
        }
    }

    override fun update(model: YoutubeChannel): Boolean {
        return getConnection { connection ->
            val sql = "UPDATE youtube_channels SET thumbnailUrl = ?, channelTitle = ?, description = ?, title = ?, subscribers = ?, youtubeUrl = ?, lastUpdated = ? WHERE channelId = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.thumbnailUrl)
                stmt.setString(2, model.channelTitle)
                stmt.setString(3, model.description)
                stmt.setString(4, model.title)
                stmt.setInt(5, model.subscribers ?: 0)
                stmt.setString(6, model.youtubeUrl)
                stmt.setString(7, model.channelId)
                stmt.setString(8, model.lastUpdated)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun delete(id: String): Boolean {
        return getConnection { connection ->
            val sql = "DELETE FROM youtube_channels WHERE channelId = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, id)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun mapRowToModel(rs: ResultSet): YoutubeChannel {
        return YoutubeChannel(
            channelId = rs.getString("channelId"),
            thumbnailUrl = rs.getString("thumbnailUrl"),
            channelTitle = rs.getString("channelTitle"),
            description = rs.getString("description"),
            title = rs.getString("title"),
            subscribers = rs.getInt("subscribers").takeIf { it != 0 },
            youtubeUrl = rs.getString("youtubeUrl"),
            lastUpdated = rs.getString("lastUpdated")
        )
    }

    override fun createTable() {
        getConnection { connection ->
            val sql = """
            CREATE TABLE IF NOT EXISTS youtube_channels (
                channelId VARCHAR(255) PRIMARY KEY,
                thumbnailUrl VARCHAR(255),
                channelTitle VARCHAR(255),
                description TEXT,
                title VARCHAR(255),
                subscribers INT,
                youtubeUrl VARCHAR(255),
                lastUpdated VARCHAR(255)
            )
        """.trimIndent()
            connection.prepareStatement(sql).use { stmt ->
                stmt?.execute()
            }
        }
    }
}
