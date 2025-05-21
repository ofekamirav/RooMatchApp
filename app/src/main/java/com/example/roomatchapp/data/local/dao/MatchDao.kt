package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.Match

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: Match)

    @Query("SELECT * FROM matches WHERE seekerId = :seekerId")
    suspend fun getMatchesBySeekerId(seekerId: String): List<Match>?

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: String): Match?

    @Query("SELECT * FROM matches WHERE propertyId = :propertyId")
    suspend fun getMatchesByPropertyId(propertyId: String): List<Match>?

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM matches")
    suspend fun clearAll()

}