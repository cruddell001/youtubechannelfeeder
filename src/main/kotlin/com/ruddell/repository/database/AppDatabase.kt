package com.ruddell.repository.database

import com.ruddell.BuildConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object AppDatabase {
    val dataSource: DataSource = initDataSource()

    private fun initDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://localhost:3306/${BuildConfig.DATABASE_NAME}"
            username = BuildConfig.DATABASE_USER
            password = BuildConfig.DATABASE_PASSWORD
            driverClassName = "com.mysql.cj.jdbc.Driver"
            maximumPoolSize = 3  // Set based on your requirements
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    val channelHelper = ChannelHelper()
    val searchHelper = ChannelSearchHelper()
    val videoHelper = VideoHelper()
    val transcriptHelper = TranscriptHelper()
    val transcriptTextHelper = TranscriptTextHelper()

    private val helpers = listOf(channelHelper, searchHelper, videoHelper, transcriptHelper, transcriptTextHelper)

    init {
        helpers.forEach { it.createTable() }
    }
}
