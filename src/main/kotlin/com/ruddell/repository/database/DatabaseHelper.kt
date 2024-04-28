package com.ruddell.repository.database

import com.ruddell.models.DbConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import javax.sql.DataSource
import kotlin.Exception

abstract class DatabaseHelper<T> {
    fun <U>getConnection(block: (Connection) -> U): U = AppDatabase.dataSource.connection.use(block)
    abstract fun mapRowToModel(rs: ResultSet): T
    abstract fun insert(model: T): Boolean
    abstract fun read(id: String): T?
    abstract fun getAll(): List<T>
    abstract fun update(model: T): Boolean
    abstract fun delete(id: String): Boolean
    abstract fun createTable()

}
