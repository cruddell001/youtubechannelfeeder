package com.ruddell.repository.database

import com.ruddell.extensions.log
import com.ruddell.extensions.toMySqlString
import com.ruddell.models.AnalyticEvent
import com.ruddell.models.AnalyticsItem
import kotlinx.serialization.Serializable
import java.sql.ResultSet
import java.util.*

class AnalyticsHelper: DatabaseHelper<AnalyticsItem>() {
    override fun mapRowToModel(rs: ResultSet): AnalyticsItem = AnalyticsItem(
        id = rs.getString("id"),
        event = rs.getString("event"),
        query = rs.getString("query"),
        extraData = rs.getString("extraData"),
        dateCreated = rs.getString("dateCreated")
    )

    fun insert(event: AnalyticEvent, query: String, extraData: String): Boolean {
        log("AnalyticEvent: ${event.key}: $query")
        return insert(AnalyticsItem(event.key, query, extraData = extraData))
    }

    override fun insert(model: AnalyticsItem): Boolean {
        return getConnection { connection ->
            val sql = "INSERT INTO analytics (event, query, extraData, dateCreated) VALUES (?, ?, ?, ?)"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.event)
                stmt.setString(2, model.query)
                stmt.setString(3, model.extraData)
                stmt.setString(4, model.dateCreated)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun read(id: String): AnalyticsItem? {
        return getConnection { connection ->
            val sql = "SELECT * FROM analytics WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, id)
                val rs = stmt.executeQuery()
                if (rs != null && rs.next()) {
                    mapRowToModel(rs)
                } else null
            }
        }
    }

    override fun getAll(): List<AnalyticsItem> {
        return getConnection { connection ->
            val sql = "SELECT * FROM analytics"
            connection.prepareStatement(sql)?.use { stmt ->
                val rs = stmt.executeQuery()
                val list = mutableListOf<AnalyticsItem>()
                while (rs.next()) {
                    list.add(mapRowToModel(rs))
                }
                list
            } ?: emptyList()
        }
    }

    override fun update(model: AnalyticsItem): Boolean {
        return getConnection { connection ->
            val sql = "UPDATE analytics SET event = ?, query = ?, dateCreated = ? WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, model.event)
                stmt.setString(2, model.query)
                stmt.setString(3, model.dateCreated)
                stmt.setString(4, model.id)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun delete(id: String): Boolean {
        return getConnection { connection ->
            val sql = "DELETE FROM analytics WHERE id = ?"
            connection.prepareStatement(sql)?.use { stmt ->
                stmt.setString(1, id)
                stmt.executeUpdate() > 0
            } ?: false
        }
    }

    override fun createTable() {
        getConnection { connection ->
            val sql = """
                CREATE TABLE IF NOT EXISTS analytics (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    event VARCHAR(255),
                    query TEXT,
                    extraData TEXT,
                    dateCreated DATETIME
                )
            """.trimIndent()
            connection.prepareStatement(sql).execute()
        }
    }
}

