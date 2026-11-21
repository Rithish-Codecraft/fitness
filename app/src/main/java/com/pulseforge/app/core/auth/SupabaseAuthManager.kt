package com.pulseforge.app.core.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.abs

/**
 * Connects the Android APK directly to the shared PulseForge AI Supabase backend
 * for Google Sign-In (OAuth), Email/Password Auth, and Multi-Device Profile Sync.
 */
object SupabaseAuthManager {

    const val SUPABASE_URL = "https://qngiwslqsgnkdpwkxjza.supabase.co"
    const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFuZ2l3c2xxc2dua2Rwd2t4anphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTUyODY0MDUsImV4cCI6MjExMDg2MjQwNX0.sUZtpVgyPc7xR_sRbu4m7h6YURukwdYWPp1YJMpKF9s"
    const val REDIRECT_URI = "pulseforge://auth/callback"

    fun createGoogleSignInIntent(): Intent {
        val url = "$SUPABASE_URL/auth/v1/authorize?provider=google&redirect_to=${Uri.encode(REDIRECT_URI)}"
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    fun derivePulseForgeId(uid: String): String {
        var hash = 0L
        for (ch in uid) {
            hash = (hash * 31L + ch.code.toLong()) and 0xFFFFFFFFL
        }
        val sixDigits = 100000L + (abs(hash) % 900000L)
        return "PF-$sixDigits"
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthAccount> =
        withContext(Dispatchers.IO) {
            try {
                val body = JSONObject().apply {
                    put("email", email.trim().lowercase())
                    put("password", password)
                }
                val resp = requestJson(
                    endpoint = "$SUPABASE_URL/auth/v1/token?grant_type=password",
                    method = "POST",
                    body = body.toString()
                )
                val userObj = resp.optJSONObject("user")
                    ?: return@withContext Result.failure(IllegalArgumentException("Invalid Supabase credentials."))
                Result.success(parseUserToAuthAccount(userObj))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun signUpWithEmail(fullName: String, email: String, password: String): Result<AuthAccount> =
        withContext(Dispatchers.IO) {
            try {
                val dataObj = JSONObject().apply { put("full_name", fullName.trim()) }
                val body = JSONObject().apply {
                    put("email", email.trim().lowercase())
                    put("password", password)
                    put("data", dataObj)
                }
                val resp = requestJson(
                    endpoint = "$SUPABASE_URL/auth/v1/signup",
                    method = "POST",
                    body = body.toString()
                )
                val userObj = resp.optJSONObject("user") ?: resp
                if (!userObj.has("id")) {
                    return@withContext Result.failure(IllegalArgumentException("Could not create cloud account."))
                }
                Result.success(parseUserToAuthAccount(userObj, fullName))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun fetchUserFromAccessToken(accessToken: String): Result<AuthAccount> =
        withContext(Dispatchers.IO) {
            try {
                val conn = (URL("$SUPABASE_URL/auth/v1/user").openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("apikey", SUPABASE_ANON_KEY)
                    setRequestProperty("Authorization", "Bearer $accessToken")
                    connectTimeout = 8000
                    readTimeout = 8000
                }
                val text = conn.inputStream.bufferedReader().readText()
                val userObj = JSONObject(text)
                Result.success(parseUserToAuthAccount(userObj))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun extractAccessTokenFromCallback(uri: Uri?): String? {
        if (uri == null) return null
        if (uri.scheme != "pulseforge") return null
        val fragment = uri.fragment ?: return uri.getQueryParameter("access_token")
        return fragment.split("&")
            .mapNotNull {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2 && parts[0] == "access_token") parts[1] else null
            }
            .firstOrNull()
    }

    private fun parseUserToAuthAccount(userObj: JSONObject, fallbackName: String = "Athlete"): AuthAccount {
        val uid = userObj.optString("id")
        val email = userObj.optString("email")
        val meta = userObj.optJSONObject("user_metadata")
        val name = meta?.optString("full_name")?.takeIf { it.isNotBlank() }
            ?: meta?.optString("name")?.takeIf { it.isNotBlank() }
            ?: fallbackName.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").ifBlank { "Athlete" }

        return AuthAccount(
            userId = derivePulseForgeId(uid),
            fullName = name,
            email = email,
            passwordHash = "supabase-oauth",
            createdAt = userObj.optString("created_at").take(16).replace("T", " ")
        )
    }

    private fun requestJson(endpoint: String, method: String, body: String): JSONObject {
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            doOutput = true
            setRequestProperty("apikey", SUPABASE_ANON_KEY)
            setRequestProperty("Content-Type", "application/json")
            connectTimeout = 8000
            readTimeout = 8000
        }
        conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
        val text = stream.bufferedReader().readText()
        val json = JSONObject(text)
        if (conn.responseCode !in 200..299) {
            val msg = json.optString("error_description")
                .ifBlank { json.optString("msg") }
                .ifBlank { json.optString("message") }
                .ifBlank { "Authentication failed (${conn.responseCode})" }
            throw IllegalArgumentException(msg)
        }
        return json
    }
}
