package com.ruddell.extensions

import org.slf4j.LoggerFactory


val logger = YoutubeChannelFeeder.logger
fun log(msg: String) {
    logger.info(msg)
}

object YoutubeChannelFeeder {
    val logger = LoggerFactory.getLogger(this::class.java)
}

object Log {
    fun debug(msg: String) = log(msg)

    fun error(e: Throwable) {
        log("Error: ${e.localizedMessage}")
        // log the stack trace using Log.debug
        e.stackTrace.forEach { log(it.toString()) }
    }
}