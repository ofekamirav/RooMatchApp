package com.example.roomatchapp.di

import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.api.UserApiServiceImplementation
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppDependencies {

    private const val BASE_URL = "http://10.0.0.9:8080" //if your are using emultaor change ip to 10.0.2.2

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

    val apiService: ApiService by lazy {
        UserApiServiceImplementation(httpClient, BASE_URL)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(apiService)
    }
}
