package com.example.roomatchapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.MatchDao
import com.example.roomatchapp.data.local.dao.OwnerAnalyticsDao
import com.example.roomatchapp.data.local.dao.PropertyDao
import com.example.roomatchapp.data.local.dao.PropertyOwnerDao
import com.example.roomatchapp.data.local.dao.RoommateDao
import com.example.roomatchapp.data.model.AnalyticsResponse
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate

@Database(entities = [CacheEntity::class, Roommate::class, PropertyOwner::class, Property::class, Match::class, AnalyticsResponse::class], version = 3)
@TypeConverters(RooMatchConverters::class)
abstract class AppLocalDB : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
    abstract fun roommateDao(): RoommateDao
    abstract fun propertyOwnerDao(): PropertyOwnerDao
    abstract fun propertyDao(): PropertyDao
    abstract fun matchDao(): MatchDao
    abstract fun ownerAnalyticsDao(): OwnerAnalyticsDao
}