package com.sample.rohlik.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemizationEntryDB::class, AmountDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemizationDao(): ItemizationEntryDao
}