package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.Roommate

@Dao
interface RoommatesDao {
    @Query("SELECT * FROM roommates")
    fun getAll(): List<Roommate>

    @Query("SELECT * FROM roommates WHERE id = :id")
    fun getById(id: Int): Roommate

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(roommate: Roommate)
}