package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.SuggestedMatchEntity

@Dao
interface SuggestedMatchDao {

    @Query("SELECT * FROM suggested_matches ORDER BY timestamp ASC")
    suspend fun getAll(): List<SuggestedMatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<SuggestedMatchEntity>)

    @Query("DELETE FROM suggested_matches WHERE matchId = :matchId")
    suspend fun delete(matchId: String)

    @Query("DELETE FROM suggested_matches")
    suspend fun clearAll()
}
