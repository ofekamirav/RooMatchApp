package com.example.roomatchapp.data.remote.api

import android.util.Log
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

// Implementation of the ApiService interface methods
class ApiServiceImplementation(
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

}