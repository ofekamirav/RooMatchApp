package com.example.roomatchapp.presentation.owner

import androidx.lifecycle.ViewModel
import com.example.roomatchapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PropertiesViewModel : ViewModel() {

    private val _properties = MutableStateFlow(dummyProperties())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _navigateToAddProperty = MutableStateFlow(false)
    val navigateToAddProperty: StateFlow<Boolean> = _navigateToAddProperty.asStateFlow()

    fun onAddPropertyClick() {
        _navigateToAddProperty.value = true
    }

    fun resetNavigationFlag() {
        _navigateToAddProperty.value = false
    }

    private fun dummyProperties(): List<Property> {
        return listOf(
            Property(
                id = "102",
                ownerId = "203",
                available = false,
                type = PropertyType.APARTMENT,
                address = "Ben Yehuda St, Tel Aviv",
                latitude = 34.0522,
                longitude = -118.2437,
                title = "Cozy House with Garden",
                canContainRoommates = 2,
                CurrentRoommatesIds = listOf("303"),
                roomsNumber = 3,
                bathrooms = 1,
                floor = 1,
                size = 100,
                pricePerMonth = 1800,
                features = listOf(CondoPreference.GARDEN, CondoPreference.PARKING, CondoPreference.FURNISHED),
                photos = listOf("url3", "url4")
            ),
            Property(
                id = "101",
                ownerId = "202",
                available = true,
                type = PropertyType.APARTMENT,
                address = "Emil Zula 12 St, Tel Aviv",
                latitude = 40.7128,
                longitude = -74.0060,
                title = "Modern Apartment in City Center",
                canContainRoommates = 3,
                CurrentRoommatesIds = listOf("301", "302"),
                roomsNumber = 4,
                bathrooms = 2,
                floor = 5,
                size = 120,
                pricePerMonth = 2500,
                features = listOf(CondoPreference.BALCONY, CondoPreference.GYM, CondoPreference.PARKING),
                photos = listOf(
                    "https://en.wikipedia.org/wiki/Harry_Potter#/media/File:Harry_Potter_wordmark.svg",
                    "https://en.wikipedia.org/wiki/Wikipedia#/media/File:Wikipedia-logo-v2.svg"
                )
            )
        )
    }
}
