package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.AnalyticsResponse

@Dao
interface OwnerAnalyticsDao {
    @Query("SELECT * FROM owner_analytics WHERE ownerId = :ownerId")
    suspend fun getOwnerAnalytics(ownerId: String): AnalyticsResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analyticsResponse: AnalyticsResponse)
}