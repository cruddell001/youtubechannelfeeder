package com.ruddell.extensions

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import java.lang.Exception

val defaultSerializer = Json { ignoreUnknownKeys = true; isLenient = true }

inline fun <reified T>String.decode(): T? {
    Log.debug("decoding ${this.length} bytes into ${T::class.java.simpleName}")
    try {
        Log.debug("trying with ktor...")
        return defaultSerializer.decodeFromString<T>(this)
    } catch (e: Throwable) {
        Log.error(e)
        Log.debug("trying with jackson...")
        return try {
            JacksonFactory.getDefaultInstance().fromString(this, T::class.java)
        } catch (e: Throwable) {
            Log.error(e)
            Log.debug("failed to decode")
            Log.debug("trying with gson...")
            try {
                Gson().fromJson(this, T::class.java)
            } catch (e: Throwable) {
                Log.error(e)
                Log.debug("failed to decode")
                null
            }
        }
    }
}

inline fun <reified T>T.toJson(): String = Gson().toJson(this)
