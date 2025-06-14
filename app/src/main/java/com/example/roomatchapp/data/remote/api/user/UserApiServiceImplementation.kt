package com.example.roomatchapp.data.remote.api.user

import android.util.Log
import com.example.roomatchapp.data.model.AnalyticsResponse
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.dto.BioRequest
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.IncompleteRegistrationException
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.RefreshTokenRequest
import com.example.roomatchapp.data.remote.dto.RefreshTokenResponse
import com.example.roomatchapp.data.remote.dto.ResetPassword
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.ServerMessageResponse
import com.example.roomatchapp.data.remote.dto.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.http.isSuccess

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
            }
            Log.d("TAG", "ApiService-Response received: $response")
            if (!response.status.isSuccess()) {
                val errorResponse = response.body<Map<String, String>>()
                val errorMessage = errorResponse["error"] ?: "Unknown error"

                throw when (response.status) {
                    HttpStatusCode.BadRequest -> IllegalArgumentException(errorMessage)
                    HttpStatusCode.InternalServerError -> RuntimeException("Server error: $errorMessage")
                    else -> Exception("Unexpected error: $errorMessage")
                }
            }

            return response.body()
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
            Log.d("TAG", "ApiService-GET All Roommates Response size: ${response.size}")
            return response
        } catch (e: Exception){
            Log.e("TAG", "ApiService-GET All Roommates API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun sendResetToken(email: String, userType: String): Result<String> {
        return try {
            val response = client.post("$baseUrl/auth/request-password-reset") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "userType" to userType))
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody = response.body<ServerMessageResponse>()
                    Result.success(responseBody.message ?: "Reset code sent successfully.")
                }
                HttpStatusCode.BadRequest, HttpStatusCode.InternalServerError, HttpStatusCode.NotFound -> {
                    val errorBody = response.body<ServerMessageResponse>()
                    Result.failure(Exception(errorBody.error ?: "Failed to send reset code."))
                }
                else -> {
                    Result.failure(Exception("Unexpected response status: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-sendResetToken API call failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String, otpCode: String, newPassword: String, userType: String): Result<String> {
        return try {
            val requestBody = ResetPassword(
                email = email,
                otpCode = otpCode,
                newPassword = newPassword,
                userType = userType
            )
            Log.d("TAG", "ApiService-Sending POST to $baseUrl/auth/reset-password with body: $requestBody")
            val response = client.post("$baseUrl/auth/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            Log.d("TAG", "ApiService-Reset Password Response status: ${response.status}")

            if (response.status.isSuccess()) {
                val responseBody = response.body<ServerMessageResponse>()
                Result.success(responseBody.message ?: "Password reset successfully.")
            } else {
                val errorBody = response.body<ServerMessageResponse>()
                Result.failure(Exception(errorBody.error ?: "Failed to reset password. Invalid OTP or email.")) // התאמת הודעה
            }
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-resetPassword API call failed: ${e.message}", e)
            Result.failure(e)
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

    override suspend fun getOwnerAnalytics(ownerId: String): AnalyticsResponse? {
        Log.d("TAG", "ApiService-Sending GET request to $baseUrl/owners/analytics/$ownerId")
        try {
            val response: HttpResponse = client.get("$baseUrl/owners/analytics/$ownerId")
            Log.d("TAG", "ApiService-GET Owner Analytics Response status: ${response.status}")

            when (response.status) {
                HttpStatusCode.OK -> {
                    try {
                        val analyticsData = response.body<AnalyticsResponse>()
                        Log.d("TAG", "ApiService-GET Owner Analytics successfully parsed for 200 OK.")
                        return analyticsData
                    } catch (e: SerializationException) {
                        Log.e("TAG", "ApiService-GET Owner Analytics (200 OK) - Failed to parse response body: ${e.message}", e)
                        throw Exception("Failed to parse successful response (200 OK) from server: ${e.message}", e)
                    }
                }
                HttpStatusCode.NoContent -> {
                    val messageBody = response.bodyAsText()
                    Log.i("TAG", "ApiService-GET Owner Analytics: No content (204). Server message: \"$messageBody\". No data found for owner $ownerId.")
                    return null
                }
                HttpStatusCode.BadRequest -> {
                    val errorPayload = response.bodyAsText()
                    Log.e("TAG", "ApiService-GET Owner Analytics API call failed (400 Bad Request): $errorPayload")
                    throw Exception("Bad request to server (400): $errorPayload")
                }
                HttpStatusCode.InternalServerError -> {
                    val errorPayload = response.bodyAsText()
                    Log.e("TAG", "ApiService-GET Owner Analytics API call failed (500 Internal Server Error): $errorPayload")
                    throw Exception("Internal server error (500): $errorPayload")
                }
                else -> {
                    val errorPayload = response.bodyAsText()
                    Log.e("TAG", "ApiService-GET Owner Analytics API call failed (Status: ${response.status}): $errorPayload")
                    throw Exception("Unhandled server response (Status: ${response.status}): $errorPayload")
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-GET Owner Analytics API call failed (Outer Catch): ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateRoommate(
        seekerId: String,
        roommate: Roommate,
    ): Boolean {
        Log.d("TAG", "ApiService-Sending PUT request to $baseUrl/roommates/$seekerId")
        try {
            val response = client.put("$baseUrl/roommates/$seekerId") {
                contentType(ContentType.Application.Json)
                setBody(roommate)
            }
            Log.d("TAG", "ApiService-PUT Roommate Response status: ${response.status}")
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-PUT Roommate API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateOwner(
        ownerId: String,
        propertyOwner: PropertyOwner,
    ): Boolean {
        Log.d("TAG", "ApiService-Sending PUT request to $baseUrl/owners/$ownerId")
        try {
            val response = client.put("$baseUrl/owners/$ownerId") {
                contentType(ContentType.Application.Json)
                setBody(propertyOwner)
            }
            Log.d("TAG", "ApiService-PUT Owner Response status: ${response.status}")
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-PUT Owner API call failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun checkEmailRegistered(email: String): Boolean {
        Log.d("TAG", "ApiService-Sending post request to $baseUrl/check-email")
        try {
            val response = client.post("$baseUrl/check-email")
            {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }
            Log.d("TAG", "ApiService-POST Email Response status: ${response.status}")
            if (response.status == HttpStatusCode.OK) {
                return true
            }
            if (response.status == HttpStatusCode.NotFound) {
                return false
            }
        } catch (e: Exception) {
            Log.e("TAG", "ApiService-POST Email API call failed: ${e.message}", e)
        }
        return false
    }
}