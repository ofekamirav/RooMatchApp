package com.example.roomatchapp.data.remote.api.match

import android.util.Log
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.utils.TokenUtils
import com.example.roomatchapp.utils.TokenUtils.refreshTokenIfNeeded
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
    private val sessionManager: UserSessionManager
): MatchApiService {

    private suspend fun getValidToken(): String {
        return refreshTokenIfNeeded(sessionManager) ?: throw Exception("No valid token available")
    }

    override suspend fun getNextMatches(seekerId: String, limit: Int): List<Match> {
        Log.d("TAG","MatchApiService- getNextMatches called")
        val token = getValidToken()
        val response = client.get("$baseUrl/match/$seekerId"){
            parameter("limit", limit)
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        return if (response.status.value == 204) emptyList() else response.body()
    }

    override suspend fun likeRoommates(match: Match): Boolean {
        Log.d("TAG","MatchApiService- likeRoommates called")
        val token = getValidToken()
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
        val token = getValidToken()
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
        val response = client.get("$baseUrl/roommates/$roommateId")
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
        val response = client.get("$baseUrl/properties/$propertyId")
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- getProperty success")
            return response.body()
        }else {
            Log.d("TAG", "MatchApiService- getProperty failed")
            return null
        }
    }

    //Get all matches that specific roommate has liked
    override suspend fun getRoommateMatches(seekerId: String): List<Match>? {
        Log.d("TAG","MatchApiService- getRoommateMatches called")
        val response = client.get("$baseUrl/likes/$seekerId")
        if(response.status.value == 200){
            Log.d("TAG","MatchApiService- getRoommateMatches success")
            return response.body()
        }else{
            Log.d("TAG","MatchApiService- getRoommateMatches failed")
            return null
        }

    }


}