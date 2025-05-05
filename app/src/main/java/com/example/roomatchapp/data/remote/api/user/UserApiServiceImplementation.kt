package com.example.roomatchapp.data.remote.api.user

import android.util.Log
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.dto.BioRequest
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.IncompleteRegistrationException
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.RefreshTokenRequest
import com.example.roomatchapp.data.remote.dto.RefreshTokenResponse
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Implementation of the ApiService interface methods
class UserApiServiceImplementation(
    private val client: HttpClient,
    private val baseUrl: String
) : UserApiService {
    override suspend fun registerOwner(request: PropertyOwnerUser): UserResponse {
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
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/login with body: $request")
            val response = client.post("$baseUrl/login") {
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


    override suspend fun registerRoommate(request: RoommateUser): UserResponse {
        try {
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/roommates/register with body: $request")
            val response = client.post("$baseUrl/roommates/register") {
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

    override suspend fun geminiSuggestClicked(
        fullName: String,
        attributes: List<String>,
        hobbies: List<String>,
        work: String
    ): BioResponse {
        try {
            val request = BioRequest(
                fullName = fullName,
                attributes = attributes,
                hobbies = hobbies,
                work = work
            )

            Log.d("TAG", "ApiService-Sending POST to $baseUrl/roommates/generate-bio with body: $request")

            val response: BioResponse = client.post("$baseUrl/roommates/generate-bio") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<BioResponse>()

            Log.d("TAG", "ApiService-Response received: $response")
            return response

        } catch (e: Exception) {
            Log.e("TAG", "ApiService-API call failed: ${e.message}", e)
            throw e
        }
    }


    override suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        try {
            Log.d("TAG", "ApiService-Sending refresh token request with: $refreshToken")

            val response = client.post("$baseUrl/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }.body<RefreshTokenResponse>()

            Log.d("TAG", "ApiService-Received refreshed tokens: $response")
            return response
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-Failed to refresh token: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getPropertyOwner(propertyOwnerId: String): PropertyOwner? {
        try {
            Log.d("TAG", "ApiService-Sending GET request to $baseUrl/owners/$propertyOwnerId")
            val response = client.get("$baseUrl/owners/$propertyOwnerId"){
                contentType(ContentType.Application.Json)
            }.body<PropertyOwner>()
            Log.d("TAG", "ApiService-GET Owner Response received: $response")
            return response
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-GET Owner API call failed: ${e.message}", e)
            throw e

        }
    }

    override suspend fun getRoommate(roommateId: String): Roommate? {
        try {
            Log.d("TAG", "ApiService-Sending GET request to $baseUrl/roommates/$roommateId")
            val response = client.get("$baseUrl/roommates/$roommateId") {
                contentType(ContentType.Application.Json)
            }.body<Roommate>()
            Log.d("TAG", "ApiService-GET Roommate Response received: $response")
            return response
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-GET Roommate API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAllRoommates(): List<Roommate>? {
        try {
            Log.d("TAG", "ApiService-Sending GET request to $baseUrl/roommates")
            val response = client.get("$baseUrl/roommates") {
                contentType(ContentType.Application.Json)
            }.body<List<Roommate>>()
            Log.d("TAG", "ApiService-GET All Roommates Response received: $response")
            return response
        } catch (e: Exception){
            Log.e("TAG", "ApiService-GET All Roommates API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun googleSignIn(idToken: String): UserResponse {
        try {
            Log.d("TAG", "ApiService-GoogleSignIn Request to $baseUrl/login/oauth/google")
            val requestBody = mapOf("idToken" to idToken)

            val response = client.post("$baseUrl/login/oauth/google") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    return response.body<UserResponse>()
                }
                HttpStatusCode.Accepted -> {
                    val responseBodyText = response.bodyAsText()
                    val jsonElement = Json.parseToJsonElement(responseBodyText).jsonObject

                    val email = jsonElement["email"]?.jsonPrimitive?.content ?: ""
                    val fullName = jsonElement["fullName"]?.jsonPrimitive?.content ?: ""
                    val profilePicture = jsonElement["profilePicture"]?.jsonPrimitive?.contentOrNull // Handle null correctly

                    throw IncompleteRegistrationException(
                        email = email,
                        fullName = fullName,
                        profilePicture = profilePicture
                    )
                }
                else -> {
                    val errorMsg = try {
                        response.body<Map<String,String>>()["error"] ?: "Unknown error"
                    } catch (e: Exception) {
                        "Server returned status ${response.status}"
                    }
                    throw Exception("Google Sign-In failed: Status ${response.status} - $errorMsg") // זרוק חריגה כללית או ספציפית יותר
                }
            }

        } catch (e: Exception) {
            Log.e("TAG", "UserApiServiceImplementation-Google Sign In failed", e)
            if (e is IncompleteRegistrationException) {
                throw e // Throw it again so ViewModel can catch it specifically
            }
            throw Exception("Google Sign-In failed: ${e.message}", e)
        }
    }
}