package com.example.roomatchapp.data.remote.api.like

import android.util.Log
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.di.AppDependencies
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LikeApiServiceImplementation(
    private val client: HttpClient,
    private val baseUrl: String
): LikeApiService {

    override suspend fun fullLike(match: Match): Boolean {
        Log.d("TAG", "LikeApiServiceImplementation - fullLike called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.post("$baseUrl/likes") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(match)
        }

        return if (response.status.value in 200..299) {
            Log.d("TAG", "LikeApiServiceImplementation - fullLike success with status ${response.status}")
            true
        } else {
            Log.d("TAG", "LikeApiServiceImplementation - fullLike failed with status ${response.status}")
            false
        }
    }

    override suspend fun dislike(match: Match): Boolean {
        Log.d("TAG", "LikeApiServiceImplementation - dislike called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.post("$baseUrl/dislike") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(match)
        }

        return if (response.status.value in 200..299) {
            Log.d("TAG", "LikeApiServiceImplementation - dislike success with status ${response.status}")
            true
        } else {
            Log.d("TAG", "LikeApiServiceImplementation - dislike failed with status ${response.status}")
            false
        }
    }

    override suspend fun getRoommateMatches(seekerId: String): List<Match>? {
        Log.d("TAG", "LikeApiServiceImplementation - getRoommateMatches called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.get("$baseUrl/likes/$seekerId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }

        return if (response.status.value in 200..299) {
            Log.d("TAG", "LikeApiServiceImplementation - getRoommateMatches success with status ${response.status}")
            response.body()
        } else {
            Log.d("TAG", "LikeApiServiceImplementation - getRoommateMatches failed with status ${response.status}")
            null
        }
    }
}
