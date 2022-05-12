package com.sample.rohlik.db

import androidx.room.*

@Dao
interface ItemizationEntryDao {

    @Query("SELECT * FROM ItemizationEntryDB")
    fun getAllItems(): List<ItemizationEntryDB>

    //TODO
//    @Query("SELECT * FROM ItemizationEntryDB")
//    fun getModifiedItems(states: Array<String>): List<ItemizationEntryDB>

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