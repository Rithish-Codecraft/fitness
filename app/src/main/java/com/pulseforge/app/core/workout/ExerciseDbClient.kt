package com.pulseforge.app.core.workout

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class ExerciseDbEntry(
    val id: String = "",
    val name: String = "",
    val bodyPart: String = "",
    val target: String = "",
    val equipment: String = "",
    val gifUrl: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList()
)

/**
 * RapidAPI ExerciseDB client for 1,300+ exercises with watermark-free animated GIF URLs
 * and highlighted target/secondary muscle metadata.
 */
class ExerciseDbClient(context: Context) {

    companion object {
        private const val HOST = "exercisedb.p.rapidapi.com"
        private const val PREFS_NAME = "pulseforge_exercisedb_prefs"
        private const val KEY_RAPIDAPI = "rapidapi_key"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSavedApiKey(): String = prefs.getString(KEY_RAPIDAPI, "") ?: ""

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_RAPIDAPI, key.trim()).apply()
    }

    suspend fun searchExercises(
        query: String = "",
        bodyPart: String = "all"
    ): Result<List<ExerciseDbEntry>> = withContext(Dispatchers.IO) {
        val apiKey = getSavedApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Connect your RapidAPI ExerciseDB key first."))
        }

        val cleanQuery = query.trim().lowercase()
        val path = when {
            cleanQuery.length >= 2 -> {
                val encoded = URLEncoder.encode(cleanQuery, "UTF-8").replace("+", "%20")
                "/exercises/name/$encoded?limit=24&offset=0"
            }
            bodyPart.isNotBlank() && bodyPart != "all" -> {
                val encoded = URLEncoder.encode(bodyPart, "UTF-8").replace("+", "%20")
                "/exercises/bodyPart/$encoded?limit=24&offset=0"
            }
            else -> "/exercises?limit=24&offset=0"
        }

        return@withContext try {
            val url = URL("https://$HOST$path")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("x-rapidapi-key", apiKey)
                setRequestProperty("x-rapidapi-host", HOST)
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                return@withContext Result.failure(
                    Exception(if (code == 401 || code == 403) "Invalid RapidAPI ExerciseDB key." else "ExerciseDB HTTP $code")
                )
            }
            val json = conn.inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<ExerciseDbEntry>>() {}.type
            val list = gson.fromJson<List<ExerciseDbEntry>>(json, type) ?: emptyList()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
