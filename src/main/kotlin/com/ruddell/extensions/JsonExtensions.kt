package com.ruddell.extensions

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.gson.Gson
import java.lang.Exception

inline fun <reified T>String.decode(): T? {
    return try { JacksonFactory.getDefaultInstance().fromString(this, T::class.java) } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T>T.toJson(): String = Gson().toJson(this)
