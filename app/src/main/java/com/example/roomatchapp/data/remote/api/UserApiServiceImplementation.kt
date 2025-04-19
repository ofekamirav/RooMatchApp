package com.example.roomatchapp.data.remote.api

import android.util.Log
import com.example.roomatchapp.data.remote.dto.Attribute
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.Hobby
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.RoommateUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

// Implementation of the ApiService interface methods
class UserApiServiceImplementation(
    private val client: HttpClient,
    private val baseUrl: String
) : ApiService {
    override suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse {
        try {
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/owners/register with body: $request")
            val response = client.post("$baseUrl/owners/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<UserResponse>()
            Log.d("TAG", "ApiService-Response received: $response")
            return response
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun login(
        request: LoginRequest
    ): UserResponse {
        try {
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/login with body: ${request.email}, $request.password}")
            val response = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to request.email, "password" to request.password))
            }.body<UserResponse>()
            Log.d("TAG", "ApiService-Response received: $response")
            return response
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-API call failed: ${e.message}", e)
            throw e

        }

    }

    override suspend fun registerRoommate(request: RoommateUserRequest): UserResponse {
        TODO("Not yet implemented")

    }

    override suspend fun geminiSuggestClicked(
        fullName: String,
        attributes: List<Attribute>,
        hobbies: List<Hobby>,
        work: String
    ): BioResponse {
        try{
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/generate-bio with body: $fullName, $attributes, $hobbies, $work")
            val response = client.post("$baseUrl/generate-bio") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("fullName" to fullName, "attributes" to attributes, "hobbies" to hobbies, "work" to work))
            }.body<String>()
            Log.d("TAG", "ApiService-Response received: $response")
            return BioResponse(response)
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-API call failed: ${e.message}", e)
            throw e
        }
    }

}