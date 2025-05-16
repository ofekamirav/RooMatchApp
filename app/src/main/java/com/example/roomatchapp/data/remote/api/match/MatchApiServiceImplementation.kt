package com.example.roomatchapp.data.remote.api.match

import android.util.Log
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.utils.TokenUtils
import com.example.roomatchapp.utils.TokenUtils.refreshTokenIfNeeded
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first

class MatchApiServiceImplementation(
    private val client: HttpClient,
    private val baseUrl: String,
): MatchApiService {

    override suspend fun getNextMatches(seekerId: String, limit: Int): List<Match> {
        Log.d("TAG", "MatchApiService - getNextMatches called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        return try {
            val response = client.get("$baseUrl/match/$seekerId") {
                parameter("limit", limit)
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
            Log.d("TAG", "MatchApiService - status: ${response.status}")
            if (response.status.value == 204) {
                emptyList()
            } else {
                val body = response.body<List<Match>>()
                Log.d("TAG", "MatchApiService - parsed matches: ${body.size}")
                body
            }
        } catch (e: Exception) {
            Log.e("TAG", "MatchApiService - error: ${e.message}", e)
            emptyList()
        }
    }


    override suspend fun likeRoommates(match: Match): Boolean {
        Log.d("TAG","MatchApiService- likeRoommates called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response =  client.post("$baseUrl/likes/roommates") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(match)
        }
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- likeRoommates success")
            return true
        }else{
            Log.d("TAG","MatchApiService- likeRoommates failed")
            return false
        }

    }

    override suspend fun likeProperty(match: Match): Boolean {
        Log.d("TAG","MatchApiService- likeProperty called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response =  client.post("$baseUrl/likes/property") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(match)
        }
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- likeProperty success")
            return true
        }else{
            Log.d("TAG","MatchApiService- likeProperty failed")
            return false
        }
    }

    override suspend fun getRoommate(roommateId: String): Roommate? {
        Log.d("TAG","MatchApiService- getRoommate called")
        val response = client.get("$baseUrl/roommates/$roommateId"){
            contentType(ContentType.Application.Json)
        }
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- getRoommate success")
            return response.body()
        }else{
            Log.d("TAG","MatchApiService- getRoommate failed")
            return null
        }
    }

    override suspend fun getProperty(propertyId: String): Property? {
        Log.d("TAG","MatchApiService- getProperty called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.get("$baseUrl/properties/$propertyId"){
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
        return when {
            response.status.value == 200 -> {
                Log.d("TAG", "MatchApiService - getProperty success")
                response.body()
            }
            response.status.value == 404 -> {
                Log.e("TAG", "MatchApiService - property not found (404)")
                null
            }
            else -> {
                Log.e("TAG", "MatchApiService - unexpected response: ${response.status}")
                null
            }
        }
    }

    override suspend fun deleteMatch(matchId: String): Boolean {
        Log.d("TAG","MatchApiService- deleteMatch called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.delete("$baseUrl/match/$matchId"){
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- deleteMatch success")
            return true
        }else{
            Log.d("TAG","MatchApiService- deleteMatch failed")
            return false
        }
    }


}