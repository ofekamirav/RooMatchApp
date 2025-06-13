package com.example.roomatchapp.data.local.session

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_session")

@Singleton
class UserSessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val HAS_SEEN_WELCOME_KEY = booleanPreferencesKey("has_seen_welcome")
        private val LAST_FETCH_MATCHES_TIMESTAMP_KEY = longPreferencesKey("last_fetch_matches_timestamp")
        private val HAS_UPDATED_PREFERENCES_KEY = booleanPreferencesKey("has_updated_preferences")

        private const val MATCH_CACHE_DURATION_MS: Long = 5 * 60 * 1000
    }

    suspend fun saveUserSession(
        token: String,
        refreshToken: String,
        userId: String,
        userType: String,
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[USER_ID_KEY] = userId
            prefs[USER_TYPE_KEY] = userType
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { prefs ->
            val hasSeenWelcome = prefs[HAS_SEEN_WELCOME_KEY] ?: false
            prefs.clear()
            prefs[HAS_SEEN_WELCOME_KEY] = hasSeenWelcome
        }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val userTypeFlow: Flow<String?> = context.dataStore.data.map { it[USER_TYPE_KEY] }
    val hasSeenWelcomeFlow: Flow<Boolean> = context.dataStore.data.map {
        it[HAS_SEEN_WELCOME_KEY] ?: false
    }
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map {
        !it[TOKEN_KEY].isNullOrEmpty()
    }

    suspend fun setHasSeenWelcome(seen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_WELCOME_KEY] = seen }
    }

    suspend fun updateLastMatchFetchTimestamp() {
        val currentTime = System.currentTimeMillis()
        context.dataStore.edit {
            it[LAST_FETCH_MATCHES_TIMESTAMP_KEY] = currentTime
        }
        Log.d("UserSessionManager", "Last match fetch timestamp updated to: $currentTime")
    }

    suspend fun forceRefetchMatchesOnNextLoad() {
        context.dataStore.edit {
            it[LAST_FETCH_MATCHES_TIMESTAMP_KEY] = 0L
        }
        Log.d("UserSessionManager", "Forcing refetch of matches on next load by resetting timestamp.")
    }

    suspend fun shouldRefetchMatches(): Boolean {
        val lastFetchTimestamp = context.dataStore.data.map {
            it[LAST_FETCH_MATCHES_TIMESTAMP_KEY] ?: 0L
        }.first()
        val shouldRefetch = (System.currentTimeMillis() - lastFetchTimestamp) > MATCH_CACHE_DURATION_MS
        Log.d("UserSessionManager", "shouldRefetchMatches: lastFetch=$lastFetchTimestamp, currentTime=${System.currentTimeMillis()}, duration=$MATCH_CACHE_DURATION_MS, result=$shouldRefetch")
        return shouldRefetch
    }

    suspend fun setUpdatedPreferencesFlag(value: Boolean) {
        context.dataStore.edit {
            it[HAS_UPDATED_PREFERENCES_KEY] = value
        }
        Log.d("UserSessionManager", "Updated preferences flag set to: $value")
    }

    suspend fun consumeUpdatedPreferencesFlag(): Boolean {
        val wasUpdated = context.dataStore.data.map {
            it[HAS_UPDATED_PREFERENCES_KEY] ?: false
        }.first()
        if (wasUpdated) {
            context.dataStore.edit {
                it[HAS_UPDATED_PREFERENCES_KEY] = false
            }
            Log.d("UserSessionManager", "Consumed updated preferences flag.")
        }
        return wasUpdated
    }

    suspend fun getUserId(): String? {
        return userIdFlow.firstOrNull()
    }
}
