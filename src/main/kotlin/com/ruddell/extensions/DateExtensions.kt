package com.ruddell.extensions

import java.text.SimpleDateFormat
import java.util.Date

val MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun Date.toMySqlString(): String = SimpleDateFormat(MYSQL_DATE_FORMAT).format(this)
fun String.toDate(): Date? = try { SimpleDateFormat(MYSQL_DATE_FORMAT).parse(this) } catch (e: Exception) { null }
