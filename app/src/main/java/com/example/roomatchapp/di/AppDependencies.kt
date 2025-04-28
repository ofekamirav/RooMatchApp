package com.example.roomatchapp.di

import android.content.Context
import androidx.room.Room
import com.example.roomatchapp.data.local.AppLocalDB
import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.api.ApiServiceImplementation
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.getValue
import kotlin.jvm.java

object AppDependencies {

    private const val BASE_URL = "http://192.168.1.158:8080" //if your are using emultaor change ip to 10.0.2.2

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
        ApiServiceImplementation(httpClient, BASE_URL)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(apiService)
    }

}
