package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.Property


@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(property: Property)

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getById(id: String): Property?

    @Query("SELECT * FROM properties WHERE ownerId = :ownerId")
    suspend fun getPropertiesByOwnerId(ownerId: String): List<Property>?

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM properties")
    suspend fun deleteAll()

    @Query("UPDATE properties SET available = :isAvailable WHERE id = :propertyId")
    suspend fun changeAvailability(propertyId: String, isAvailable: Boolean): Int

    @Query("SELECT id FROM properties")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM properties WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

}