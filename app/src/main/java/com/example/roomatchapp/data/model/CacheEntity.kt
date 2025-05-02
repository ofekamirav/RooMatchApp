package com.example.roomatchapp.data.model

import androidx.room.Entity
import com.example.roomatchapp.data.base.Constants.Collections.CACHE_ENTITIES


@Entity(tableName = CACHE_ENTITIES, primaryKeys = ["type", "entityId"])
data class CacheEntity(
    val type: CacheType,
    val entityId: String,
    val lastUpdatedAt: Long
)

enum class CacheType {
    ROOMMATE,
    PROPERTY,
    PROPERTY_OWNER,
    MATCH
}

