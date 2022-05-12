package com.sample.rohlik.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DATABASE_NAME = "itemization_db"

@Database(entities = [ItemizationEntryDB::class, AmountDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemizationDao(): ItemizationEntryDao

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME)
                // prepopulate the database after onCreate was called
                .addCallback(object : Callback() {
                })
                .build()

        }
    }
}
