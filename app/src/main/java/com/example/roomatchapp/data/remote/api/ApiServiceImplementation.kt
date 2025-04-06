package com.example.roomatchapp.data.remote.api

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
        return client.post("$baseUrl/owners/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

}