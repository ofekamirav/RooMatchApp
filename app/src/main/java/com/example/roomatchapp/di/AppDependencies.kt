package com.example.roomatchapp.di


import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.data.remote.api.match.MatchApiServiceImplementation
import com.example.roomatchapp.data.remote.api.user.UserApiService
import com.example.roomatchapp.data.remote.api.user.UserApiServiceImplementation
import com.example.roomatchapp.data.repository.MatchRepositoryImpl
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.domain.repository.MatchRepository
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

    internal const val BASE_URL = "http://192.168.1.158:8080" //if your are using emultaor change ip to 10.0.2.2

    lateinit var sessionManager: UserSessionManager


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
        UserRepositoryImpl(userApiService)
    }

    val matchApiService: MatchApiService by lazy {
        MatchApiServiceImplementation(httpClient, BASE_URL, sessionManager)
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepositoryImpl(matchApiService)
    }

}
