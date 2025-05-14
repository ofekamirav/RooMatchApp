package com.example.roomatchapp.data.remote.api.property

import android.util.Log
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.dto.PropertyDto
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.utils.TokenUtils.refreshTokenIfNeeded
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers

class PropertyApiServiceImplementation(
    private val client: HttpClient,
    private val baseUrl: String,
    private val sessionManager: UserSessionManager
): PropertyApiService {

    override suspend fun getProperty(propertyId: String): Property? {
        Log.d("TAG","PropertyApiService- getProperty called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.get("$baseUrl/properties/$propertyId"){
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        if(response.status.value == 200){
            Log.d("TAG","PropertyApiService- getProperty success")
            return response.body()
        }else {
            Log.d("TAG", "PropertyApiService- getProperty failed")
            return null
        }
    }

    override suspend fun getOwnerProperties(ownerId: String): List<Property>? {
        Log.d("TAG","PropertyApiService- getOwnerProperties called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.get("$baseUrl/properties/$ownerId"){
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        if(response.status.value == 200){
            Log.d("TAG","PropertyApiService- getOwnerProperties success")
            return response.body()
        }
        else {
            Log.d("TAG", "PropertyApiService- getOwnerProperties failed")
            return null
        }
    }

    override suspend fun addProperty(property: PropertyDto): Property? {
        Log.d("TAG","PropertyApiService- addProperty called")
        val ownerId = property.ownerId
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.post("$baseUrl/properties/${ownerId}") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(property)
        }
        if(response.status.value == 201){
            Log.d("TAG","PropertyApiService- addProperty success")
            return response.body()
        }else {
            Log.d("TAG", "PropertyApiService- addProperty failed: ${response.status.value}")
            return null
        }
    }

    override suspend fun changeAvailability(
        propertyId: String,
        isAvailable: Boolean,
    ): Boolean {
        Log.d("TAG","PropertyApiService- changeAvailability called")
        val token = AppDependencies.tokenAuthenticator.getValidToken()
        val response = client.put("$baseUrl/properties/$propertyId/availability") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(isAvailable)
        }
        if(response.status.value == 200){
            Log.d("TAG","PropertyApiService- changeAvailability success")
            return true
        }else {
            Log.d("TAG", "PropertyApiService- changeAvailability failed: ${response.status.value}")
            return false
        }
    }

}