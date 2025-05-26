package com.example.roomatchapp.data.local.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
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
        private val LAST_FETCH_MATCHES_KEY = stringPreferencesKey("last_fetch_matches")
    }

    suspend fun saveUserSession(
        token: String,
        refreshToken: String,
        userId: String,
        userType: String,
        lastFetchMatches: Long = System.currentTimeMillis()
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[USER_ID_KEY] = userId
            prefs[USER_TYPE_KEY] = userType
            prefs[LAST_FETCH_MATCHES_KEY] = lastFetchMatches.toString()
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
    val lastMatchesFetchFlow: Flow<Long> = context.dataStore.data.map {
        it[LAST_FETCH_MATCHES_KEY]?.toLongOrNull() ?: 0L
    }


    suspend fun setHasSeenWelcome(seen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_WELCOME_KEY] = seen }
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map {
        !it[TOKEN_KEY].isNullOrEmpty()
    }

    suspend fun updateLastFetchTimestamp() {
        context.dataStore.edit {
            it[LAST_FETCH_MATCHES_KEY] = System.currentTimeMillis().toString()
        }
    }

    suspend fun shouldRefetchMatches(): Boolean {
        val last = lastMatchesFetchFlow.first()
        return System.currentTimeMillis() - last > 5 * 60 * 1000
    }

    suspend fun getUserId(): String? {
        return userIdFlow.firstOrNull()
    }


}

