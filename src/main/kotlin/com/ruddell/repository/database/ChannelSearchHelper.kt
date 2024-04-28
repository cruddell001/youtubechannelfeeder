package com.ruddell.repository.database

import com.ruddell.models.YoutubeChannelSearch
import java.sql.ResultSet

/*
@Serializable
data class YoutubeChannelSearch(
    val query: String,
    val items: List<YoutubeChannel> = emptyList(),
    val lastRan: String = Date().toMySqlString()
)
 */
class ChannelSearchHelper: DatabaseHelper<YoutubeChannelSearch>() {

    override fun mapRowToModel(rs: ResultSet): YoutubeChannelSearch {
        return YoutubeChannelSearch(
            query = rs.getString("query"),
            channelIds = rs.getString("channelIds").split(",").filter { it.isNotBlank() },
            lastRan = rs.getString("lastRan")
        )
    }

    override fun insert(model: YoutubeChannelSearch): Boolean {
        return getConnection { connection ->
            val sql = "INSERT INTO youtube_channel_search (query, lastRan, channelIds) VALUES (?, ?, ?)"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.query)
                stmt.setString(2, model.lastRan)
                stmt.setString(3, model.channelIds.joinToString(","))
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun read(id: String): YoutubeChannelSearch? {
        val query = id
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_channel_search WHERE query = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt?.setString(1, query)
                val rs = stmt?.executeQuery()
                if (rs != null && rs.next()) {
                    mapRowToModel(rs)
                } else null
            }
        }
    }

    override fun getAll(): List<YoutubeChannelSearch> {
        return getConnection { connection ->
            val sql = "SELECT * FROM youtube_channel_search"
            connection.prepareStatement(sql).use { stmt ->
                val rs = stmt?.executeQuery()
                val list = mutableListOf<YoutubeChannelSearch>()
                while (rs != null && rs.next()) {
                    list.add(mapRowToModel(rs))
                }
                list
            }
        }
    }

    override fun update(model: YoutubeChannelSearch): Boolean {
        return getConnection { connection ->
            val sql = "UPDATE youtube_channel_search SET lastRan = ? WHERE query = ?"
            connection?.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.lastRan)
                stmt.setString(2, model.query)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun delete(id: String): Boolean {
        val query = id
        return getConnection { connection ->
            val sql = "DELETE FROM youtube_channel_search WHERE query = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, query)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun createTable() {
        return getConnection { connection ->
            val sql = """
            CREATE TABLE IF NOT EXISTS youtube_channel_search (
                query VARCHAR(255) PRIMARY KEY,
                channelIds TEXT,
                lastRan VARCHAR(255)
            )
        """.trimIndent()
            connection.prepareStatement(sql).use { stmt ->
                stmt?.execute()
            }
        }
    }
}