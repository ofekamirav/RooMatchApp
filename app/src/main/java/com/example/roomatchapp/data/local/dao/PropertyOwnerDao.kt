package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.PropertyOwner


@Dao
interface PropertyOwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(propertyOwner: PropertyOwner)

    @Query("SELECT * FROM owners WHERE id = :id")
    suspend fun getById(id: String): PropertyOwner?

    @Query("DELETE FROM owners WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM owners")
    suspend fun deleteAll()
}