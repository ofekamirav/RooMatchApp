package com.example.roomatchapp.di


import android.content.Context
import android.util.Log
import com.example.roomatchapp.BuildConfig
import com.example.roomatchapp.data.local.AppLocalDB
import com.example.roomatchapp.data.local.LocalDatabaseProvider
import com.example.roomatchapp.data.local.session.TokenAuthenticator
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.remote.api.like.LikeApiServiceImplementation
import com.example.roomatchapp.data.remote.api.like.LikeApiService
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.data.remote.api.match.MatchApiServiceImplementation
import com.example.roomatchapp.data.remote.api.property.PropertyApiService
import com.example.roomatchapp.data.remote.api.property.PropertyApiServiceImplementation
import com.example.roomatchapp.data.remote.api.user.UserApiService
import com.example.roomatchapp.data.remote.api.user.UserApiServiceImplementation
import com.example.roomatchapp.data.repository.LikeRepositoryImpl
import com.example.roomatchapp.data.repository.MatchRepositoryImpl
import com.example.roomatchapp.data.repository.PropertyRepositoryImpl
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.domain.repository.UserRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.Locale
import kotlin.getValue

object AppDependencies {

    const val computerIP = "10.0.2.2" //if your are using emulator change ip to 10.0.2.2

    const val dnsAddress = "roomatch.cs.colman.ac.il"

    internal const val BASE_URL = "http://$dnsAddress:8080"

    lateinit var sessionManager: UserSessionManager

    lateinit var tokenAuthenticator: TokenAuthenticator

    lateinit var googlePlacesClient: PlacesClient

    lateinit var localDB: AppLocalDB

    fun init(context: Context){
        localDB = LocalDatabaseProvider.getDatabase(context)
        tokenAuthenticator = TokenAuthenticator(sessionManager, BASE_URL, httpClient)
        initPlaces(context)
    }


    val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }

            expectSuccess = false

            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status == HttpStatusCode.Unauthorized) {
                        val refreshed = tokenAuthenticator.refreshToken()
                        if (refreshed == null) {
                            Log.e("Token", "Unable to refresh session, logging out")
                            throw ClientRequestException(response, "Session expired")
                        }
                    }
                }
            }


        }
    }

    fun initPlaces(context: Context) {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.GOOGLE_PLACES_API_KEY, Locale.ENGLISH)
        }
        googlePlacesClient = Places.createClient(context)
    }


    val userApiService: UserApiService by lazy {
        UserApiServiceImplementation(httpClient, BASE_URL)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userApiService,
            localDB.roommateDao(),
            localDB.propertyOwnerDao(),
            localDB.cacheDao(),
            localDB.ownerAnalyticsDao()
        )
    }

    val matchApiService: MatchApiService by lazy {
        MatchApiServiceImplementation(httpClient, BASE_URL)
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepositoryImpl(
            matchApiService,
            localDB.roommateDao(),
            localDB.propertyDao(),
            localDB.cacheDao(),
            localDB.matchDao(),
            localDB.suggestedMatchDao(),
            sessionManager
        )
    }

    val propertyApiService: PropertyApiService by lazy {
        PropertyApiServiceImplementation(httpClient, BASE_URL)
    }

    val propertyRepository: PropertyRepository by lazy {
        PropertyRepositoryImpl(
            propertyApiService,
            localDB.cacheDao(),
            localDB.propertyDao()
        )
    }

    val likeApiService: LikeApiService by lazy {
        LikeApiServiceImplementation(httpClient, BASE_URL)
    }

    val likeRepository: LikeRepository by lazy {
        LikeRepositoryImpl(
            likeApiService,
            localDB.cacheDao(),
            localDB.matchDao()
        )
    }
}
