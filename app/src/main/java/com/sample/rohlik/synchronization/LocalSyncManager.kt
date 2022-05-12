package com.sample.rohlik.synchronization

import com.sample.rohlik.db.AppDatabase
import com.sample.rohlik.db.ItemizationEntryDB

class LocalSyncManager constructor(private val database: AppDatabase) {

    fun getItemizationEntries(): List<ItemizationEntryDB> {
        return database.itemizationDao().getAllItems()
    }

    //TODO
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