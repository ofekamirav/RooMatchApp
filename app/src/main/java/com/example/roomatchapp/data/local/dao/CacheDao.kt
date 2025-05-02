package com.example.roomatchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType

@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cacheEntity: CacheEntity)

    @Query("SELECT * FROM cache_entities WHERE entityId = :entityId AND type = :type")
    suspend fun getByIdAndType(entityId: String, type: CacheType): CacheEntity?

    @Query("SELECT * FROM cache_entities WHERE type = :type")
    suspend fun getAllByType(type: CacheType): List<CacheEntity>

    @Query("DELETE FROM cache_entities WHERE entityId = :entityId")
    suspend fun delete(entityId: String)

    @Query("SELECT * FROM cache_entities WHERE entityId = :entityId")
    suspend fun getByEntityId(entityId: String): CacheEntity?

    @Query("DELETE FROM cache_entities")
    suspend fun deleteAll()

    @Query("DELETE FROM cache_entities WHERE type = :type")
    suspend fun clearByType(type: CacheType)

    @Query("SELECT lastUpdatedAt FROM cache_entities WHERE type = :type ORDER BY lastUpdatedAt DESC LIMIT 1")
    suspend fun getLastUpdatedAt(type: CacheType): Long?

}