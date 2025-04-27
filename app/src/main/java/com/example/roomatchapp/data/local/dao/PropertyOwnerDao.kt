package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.PropertyOwner

@Dao
interface PropertyOwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPropertyOwner(propertyOwner: PropertyOwner)

    @Query("SELECT * FROM owners WHERE id = :id")
    fun getById(id: String): PropertyOwner?

}