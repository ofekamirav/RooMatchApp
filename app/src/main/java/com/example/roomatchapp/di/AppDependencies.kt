package com.example.roomatchapp.di


import android.content.Context
import com.example.roomatchapp.data.local.AppLocalDB
import com.example.roomatchapp.data.local.LocalDatabaseProvider
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.data.remote.api.match.MatchApiServiceImplementation
import com.example.roomatchapp.data.remote.api.property.PropertyApiService
import com.example.roomatchapp.data.remote.api.property.PropertyApiServiceImplementation
import com.example.roomatchapp.data.remote.api.user.UserApiService
import com.example.roomatchapp.data.remote.api.user.UserApiServiceImplementation
import com.example.roomatchapp.data.repository.MatchRepositoryImpl
import com.example.roomatchapp.data.repository.PropertyRepositoryImpl
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.getValue

object AppDependencies {

    const val computerIP = "192.168.1.158" //if your are using emulator change ip to 10.0.2.2

    internal const val BASE_URL = "http://$computerIP:8080"

    lateinit var sessionManager: UserSessionManager

    lateinit var localDB: AppLocalDB

    fun init(context: Context){
        localDB = LocalDatabaseProvider.getDatabase(context)
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
                level = LogLevel.ALL
            }
        }
    }

    val userApiService: UserApiService by lazy {
        UserApiServiceImplementation(httpClient, BASE_URL)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userApiService,
            localDB.roommateDao(),
            localDB.propertyOwnerDao(),
            localDB.cacheDao()
        )
    }

    val matchApiService: MatchApiService by lazy {
        MatchApiServiceImplementation(httpClient, BASE_URL, sessionManager)
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepositoryImpl(
            matchApiService,
            localDB.cacheDao(),
            localDB.matchDao()
        )
    }

    val propertyApiService: PropertyApiService by lazy {
        PropertyApiServiceImplementation(httpClient, BASE_URL, sessionManager)
    }

    val propertyRepository: PropertyRepository by lazy {
        PropertyRepositoryImpl(
            propertyApiService,
            localDB.cacheDao(),
            localDB.propertyDao()
        )
    }
}
