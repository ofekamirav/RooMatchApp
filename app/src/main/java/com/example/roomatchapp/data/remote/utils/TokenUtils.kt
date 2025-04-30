package com.example.roomatchapp.data.remote.utils

import android.util.Base64
import android.util.Log
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.di.AppDependencies
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.*

object TokenUtils {

    private const val baseUrl = AppDependencies.BASE_URL
    private const val REFRESH_ENDPOINT = "http://$baseUrl/auth/refresh"

    fun isTokenAboutToExpire(token: String?, thresholdInSeconds: Long = 30): Boolean {
        return try {
            if (token == null) return true
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            val json = Json.parseToJsonElement(payloadJson).jsonObject
            val exp = json["exp"]?.jsonPrimitive?.longOrNull ?: return true
            val now = System.currentTimeMillis() / 1000
            exp - now < thresholdInSeconds
        } catch (e: Exception) {
            true // treat as expired on any failure
        }
    }

    suspend fun refreshTokenIfNeeded(sessionManager: UserSessionManager): String? {
        val token = sessionManager.tokenFlow.firstOrNull()
        val refreshToken = sessionManager.refreshTokenFlow.firstOrNull()

        if (token.isNullOrEmpty() || refreshToken.isNullOrEmpty()) return null

        if (!isTokenAboutToExpire(token)) {
            return token // not expired, return the current token
        }

        //Send request to refresh token
        try {
            val response: HttpResponse = HttpClient(CIO).post(REFRESH_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }

            if (response.status == HttpStatusCode.OK) {
                val json = Json { ignoreUnknownKeys = true }
                val body = json.decodeFromString<Map<String, String>>(response.bodyAsText())
                val newToken = body["token"]
                val newRefreshToken = body["refreshToken"]

                if (!newToken.isNullOrEmpty() && !newRefreshToken.isNullOrEmpty()) {
                    //Save the new tokens
                    sessionManager.saveUserSession(
                        token = newToken,
                        refreshToken = newRefreshToken,
                        userId = sessionManager.userIdFlow.first() ?: "",
                        userType = sessionManager.userTypeFlow.first() ?: ""
                    )
                    return newToken
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "TokenUtils-Refresh token failed: ${e.message}")
        }

        return null
    }
}
