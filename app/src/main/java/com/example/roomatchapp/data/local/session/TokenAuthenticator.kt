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
    private val baseUrl: String,
    private val client: HttpClient
) {

    suspend fun getValidToken(): String {
        val token = sessionManager.tokenFlow.first()

        val isExpired = try {
            val decodedJWT = JWT.decode(token)
            val expiresAt = decodedJWT.expiresAt
            expiresAt?.before(Date()) ?: true
        } catch (e: Exception) {
            true
        }

        return if (!isExpired && !token.isNullOrBlank()) {
            token
        } else {
            refreshToken() ?: throw Exception("Missing tokens – cannot refresh session")
        }
    }


    suspend fun refreshToken(): String? {
        val refreshToken = sessionManager.refreshTokenFlow.first()
        val accessToken = sessionManager.tokenFlow.first()

        if (refreshToken.isNullOrBlank() || accessToken.isNullOrBlank()) {
            return null
        }

        val response = client.post("$baseUrl/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RequestRefresh(accessToken, refreshToken))
        }

        return if (response.status == HttpStatusCode.OK) {
            val body = response.bodyAsText()
            val json = JSONObject(body)
            val newAccessToken = json.getString("accessToken")
            val newRefreshToken = json.getString("refreshToken")

            sessionManager.saveUserSession(
                token = newAccessToken,
                refreshToken = newRefreshToken,
                userId = sessionManager.userIdFlow.first() ?: "",
                userType = sessionManager.userTypeFlow.first() ?: ""
            )

            newAccessToken
        } else {
            null
        }
    }
}

