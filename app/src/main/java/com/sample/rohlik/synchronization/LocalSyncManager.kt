package com.sample.rohlik.synchronization

import android.app.Application
import androidx.room.Room
import com.sample.rohlik.db.AppDatabase
import com.sample.rohlik.db.ItemizationEntryDB
import java.lang.Exception

class LocalSyncManager constructor(private val application: Application) {

    private val database: AppDatabase = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "itemization_db"
    ).build()

    fun getItemizationEntries(): List<ItemizationEntryDB> {
        return database.itemizationDao().getAllItems()
    }

    fun getLocallyModifiedItems(): List<ItemizationEntryDB> {
        return database.itemizationDao().getAllItems()
    }

    fun save(item: ItemizationEntryDB) : Boolean {
        try {
            database.itemizationDao().insert(item)
        } catch (ex: Exception){
            // Track exception
            return false
        }
        return true
    }

    fun saveAll(items: List<ItemizationEntryDB>) : Boolean {
        var isSaveAllSuccessful = true
        items.forEach { item ->
            try {
                save(item)
            } catch (ex: Exception){
                // Track exception
                isSaveAllSuccessful = false
            }
        }
        return isSaveAllSuccessful
    }

    fun updateStateAndSave(id: String, state: ObjectState){
        database.itemizationDao().updateState(id, state.text)
    }

    fun delete(id: String){
        database.itemizationDao().run {
            delete(findById(id))
        }
    }
}