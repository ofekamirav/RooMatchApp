package com.example.roomatchapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roomatchapp.data.local.dao.RoommatesDao

//@Database()
abstract class AppLocalDB : RoomDatabase() {
    abstract fun roommatesDao(): RoommatesDao
}