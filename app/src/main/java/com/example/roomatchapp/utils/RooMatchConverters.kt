package com.example.roomatchapp.utils

import androidx.room.TypeConverter
import com.example.roomatchapp.data.model.Attribute
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.Hobby
import com.example.roomatchapp.data.model.LookingForCondoPreference
import com.example.roomatchapp.data.model.LookingForRoomiesPreference
import com.example.roomatchapp.data.model.PropertyMatchAnalytics
import com.example.roomatchapp.data.model.RoommateMatch
import kotlinx.serialization.json.Json

object RooMatchConverters {

    private val json = Json {
        ignoreUnknownKeys = true // if we will add new fields in the future
    }

    // ---- List<String> ----
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    // ---- List<RoommateMatch> ----
    @TypeConverter
    fun fromRoommateMatchList(value: List<RoommateMatch>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toRoommateMatchList(value: String): List<RoommateMatch> {
        return json.decodeFromString(value)
    }

    // ---- List<Attribute> ----
    @TypeConverter
    fun fromAttributeList(value: List<Attribute>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toAttributeList(value: String): List<Attribute> {
        return json.decodeFromString(value)
    }

    // ---- List<Hobby> ----
    @TypeConverter
    fun fromHobbyList(value: List<Hobby>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toHobbyList(value: String): List<Hobby> {
        return json.decodeFromString(value)
    }

    // ---- List<LookingForRoomiesPreference> ----
    @TypeConverter
    fun fromLookingForRoomiesPreferenceList(value: List<LookingForRoomiesPreference>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toLookingForRoomiesPreferenceList(value: String): List<LookingForRoomiesPreference> {
        return json.decodeFromString(value)
    }

    // ---- List<LookingForCondoPreference> ----
    @TypeConverter
    fun fromLookingForCondoPreferenceList(value: List<LookingForCondoPreference>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toLookingForCondoPreferenceList(value: String): List<LookingForCondoPreference> {
        return json.decodeFromString(value)
    }

    // ---- List<CondoPreference> ----
    @TypeConverter
    fun fromCondoPreferenceList(value: List<CondoPreference>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toCondoPreferenceList(value: String): List<CondoPreference> {
        return json.decodeFromString(value)
    }

    // ---- List<PropertyMatchAnalytics> ----
    @TypeConverter
    fun fromPropertyMatchAnalyticsList(value: List<PropertyMatchAnalytics>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toPropertyMatchAnalyticsList(value: String): List<PropertyMatchAnalytics> {
        return json.decodeFromString(value)
    }


}