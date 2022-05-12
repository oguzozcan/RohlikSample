package com.sample.rohlik.db

import androidx.room.*
import com.sample.rohlik.synchronization.ObjectState

@Dao
interface ItemizationEntryDao {

    @Query("SELECT * FROM ItemizationEntryDB")
    fun getAllItems(): List<ItemizationEntryDB>

    @Query("SELECT * FROM ItemizationEntryDB")
    fun getModifiedItems(states: Array<ObjectState>): List<ItemizationEntryDB>

    @Query("SELECT * FROM ItemizationEntryDB WHERE expenseId == :id")
    fun findById(id: String): ItemizationEntryDB

    @Insert
    fun insert(entry: ItemizationEntryDB)

    @Delete
    fun delete(entry: ItemizationEntryDB)

    @Update
    fun updateState(id: String, state: String){
        //TODO
        //findById(id).state = state
    }
}