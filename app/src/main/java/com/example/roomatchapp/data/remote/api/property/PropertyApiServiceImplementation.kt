package com.example.roomatchapp.data.remote.api.property

import android.util.Log
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.utils.TokenUtils.refreshTokenIfNeeded
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

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

}