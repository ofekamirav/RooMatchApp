package com.example.roomatchapp.data.local

import android.content.Context
import androidx.room.Room

object LocalDatabaseProvider {

    @Volatile
    private var INSTANCE: AppLocalDB? = null

    fun getDatabase(context: Context): AppLocalDB {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppLocalDB::class.java,
                "roomatch_db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
