package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.Roommate

@Dao
interface RoommateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roommate: Roommate)

    @Query("SELECT * FROM roommates WHERE id = :id")
    suspend fun getById(id: String): Roommate?

    @Query("DELETE FROM roommates WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM roommates")
    suspend fun deleteAll()

}