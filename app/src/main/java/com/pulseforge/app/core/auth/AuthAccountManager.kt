package com.pulseforge.app.core.auth

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

data class AuthAccount(
    val userId: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val createdAt: String
)

data class AuthSessionState(
    val isLoggedIn: Boolean = false,
    val activeAccount: AuthAccount? = null,
    val needsPersonalDetailsWizard: Boolean = false
)

/**
 * Local-first User Authentication & User ID Manager.
 * Generates unique PulseForge User IDs (PF-XXXXXX), hashes passwords with SHA-256,
 * and tracks whether the newly signed-up user has completed the personal details wizard.
 */
class AuthAccountManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "pulseforge_auth_prefs"
        private const val KEY_ACCOUNTS_JSON = "registered_accounts_json"
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_NEEDS_WIZARD = "needs_personal_wizard"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSessionState(): AuthSessionState {
        val activeId = prefs.getString(KEY_ACTIVE_USER_ID, null) ?: return AuthSessionState()
        val account = getAccounts().firstOrNull { it.userId == activeId } ?: return AuthSessionState()
        val needsWizard = prefs.getBoolean(KEY_NEEDS_WIZARD, false)
        return AuthSessionState(
            isLoggedIn = true,
            activeAccount = account,
            needsPersonalDetailsWizard = needsWizard
        )
    }

    fun signUp(fullName: String, email: String, password: String): Result<AuthAccount> {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        if (cleanName.length < 2) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (cleanEmail.isBlank() || (!cleanEmail.contains("@") && cleanEmail.length < 6)) {
            return Result.failure(IllegalArgumentException("Please enter a valid email or phone."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val accounts = getAccounts().toMutableList()
        if (accounts.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("An account with this email already exists. Please log in."))
        }

        val existingIds = accounts.map { it.userId }.toSet()
        var newId: String
        do {
            newId = "PF-${Random.nextInt(100000, 999999)}"
        } while (newId in existingIds)

        val account = AuthAccount(
            userId = newId,
            fullName = cleanName,
            email = cleanEmail,
            passwordHash = sha256(password),
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        )
        accounts.add(account)
        saveAccounts(accounts)

        prefs.edit()
            .putString(KEY_ACTIVE_USER_ID, account.userId)
            .putBoolean(KEY_NEEDS_WIZARD, true)
            .apply()

        return Result.success(account)
    }

    fun login(identifier: String, password: String): Result<AuthAccount> {
        val cleanId = identifier.trim()
        if (cleanId.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Enter your Email / User ID and password."))
        }

        val account = getAccounts().firstOrNull {
            it.email.equals(cleanId, ignoreCase = true) || it.userId.equals(cleanId, ignoreCase = true)
        } ?: return Result.failure(IllegalArgumentException("No account found for \"$cleanId\". Please sign up first."))

        if (account.passwordHash != sha256(password)) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        prefs.edit()
            .putString(KEY_ACTIVE_USER_ID, account.userId)
            .putBoolean(KEY_NEEDS_WIZARD, false)
            .apply()

        return Result.success(account)
    }

    fun markPersonalDetailsCompleted() {
        prefs.edit().putBoolean(KEY_NEEDS_WIZARD, false).apply()
    }

    fun setExternalAuthenticatedAccount(account: AuthAccount, isNew: Boolean) {
        val accounts = getAccounts().filterNot { it.userId == account.userId }.toMutableList()
        accounts.add(account)
        saveAccounts(accounts)
        prefs.edit()
            .putString(KEY_ACTIVE_USER_ID, account.userId)
            .putBoolean(KEY_NEEDS_WIZARD, isNew)
            .apply()
    }

    fun signInWithGoogle(googleEmail: String): Result<Pair<AuthAccount, Boolean>> {
        val cleanEmail = googleEmail.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Select a valid Google account."))
        }

        val accounts = getAccounts().toMutableList()
        val existing = accounts.firstOrNull { it.email.equals(cleanEmail, ignoreCase = true) }
        if (existing != null) {
            prefs.edit()
                .putString(KEY_ACTIVE_USER_ID, existing.userId)
                .putBoolean(KEY_NEEDS_WIZARD, false)
                .apply()
            return Result.success(existing to false)
        }

        val existingIds = accounts.map { it.userId }.toSet()
        var newId: String
        do {
            newId = "PF-${Random.nextInt(100000, 999999)}"
        } while (newId in existingIds)

        val derivedName = cleanEmail.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.replaceFirstChar { ch -> ch.uppercase() }
            }
            .ifBlank { "Google Member" }

        val account = AuthAccount(
            userId = newId,
            fullName = derivedName,
            email = cleanEmail,
            passwordHash = sha256("google_oauth_${newId}"),
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        )
        accounts.add(account)
        saveAccounts(accounts)

        prefs.edit()
            .putString(KEY_ACTIVE_USER_ID, account.userId)
            .putBoolean(KEY_NEEDS_WIZARD, true)
            .apply()

        return Result.success(account to true)
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_ACTIVE_USER_ID)
            .putBoolean(KEY_NEEDS_WIZARD, false)
            .apply()
    }

    private fun getAccounts(): List<AuthAccount> {
        val json = prefs.getString(KEY_ACCOUNTS_JSON, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<AuthAccount>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveAccounts(accounts: List<AuthAccount>) {
        prefs.edit().putString(KEY_ACCOUNTS_JSON, gson.toJson(accounts)).apply()
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
