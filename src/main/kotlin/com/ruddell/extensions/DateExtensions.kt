package com.ruddell.extensions

import java.text.SimpleDateFormat
import java.util.Date

val MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

val RSS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

val RFC_822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"

fun Date.toMySqlString(): String = SimpleDateFormat(MYSQL_DATE_FORMAT).format(this)
fun Date.toRssString(): String = SimpleDateFormat(RSS_DATE_FORMAT).format(this)

fun Date.toRfc822(): String = SimpleDateFormat(RFC_822_DATE_FORMAT).format(this)

fun String.toDate(format: String = MYSQL_DATE_FORMAT): Date? = try { SimpleDateFormat(MYSQL_DATE_FORMAT).parse(this) } catch (e: Exception) { null }
