package com.example.roomatchapp.data.local.session

import com.example.roomatchapp.data.remote.dto.RequestRefresh
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.Date
import com.auth0.jwt.JWT

class TokenAuthenticator(
    private val sessionManager: UserSessionManager,
    private val baseUrl: String
) {

    suspend fun getValidToken(): String? {
        val token = sessionManager.tokenFlow.first()

        if (!token.isNullOrBlank()) {
            val isExpired = try {
                val decodedJWT = JWT.decode(token)
                val expiresAt = decodedJWT.expiresAt
                expiresAt?.before(Date()) ?: true
            } catch (e: Exception) {
                true // אם לא ניתן לפענח – תתייחס כלא תקף
            }

            if (!isExpired) {
                return token
            }
        }

        // טוקן ריק או שפג תוקף => מנסה לרענן
        return refreshToken()
    }

    suspend fun refreshToken(): String? {
        val refreshToken = sessionManager.refreshTokenFlow.first() ?: return null
        val accessToken = sessionManager.tokenFlow.first() ?: return null
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val response: HttpResponse = client.post("$baseUrl/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RequestRefresh(
                accessToken = accessToken,
                refreshToken = refreshToken
            ))
        }

        return if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            val json = JSONObject(responseBody)
            val newToken = json.getString("accessToken")
            val newRefresh = json.getString("refreshToken")

            sessionManager.saveUserSession(
                token = newToken,
                refreshToken = newRefresh,
                userId = sessionManager.userIdFlow.first() ?: "",
                userType = sessionManager.userTypeFlow.first() ?: ""
            )
            newToken
        } else {
            null
        }
    }
}
