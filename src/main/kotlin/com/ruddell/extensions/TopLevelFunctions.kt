package com.ruddell.extensions

import org.slf4j.LoggerFactory


val logger = YoutubeChannelFeeder.logger
fun log(msg: String) {
    logger.info(msg)
}

object YoutubeChannelFeeder {
    val logger = LoggerFactory.getLogger(this::class.java)
}