package com.sample.rohlik.synchronization

import android.app.Application
import android.util.Log
import com.sample.rohlik.data.NetworkResponse
import com.sample.rohlik.db.AppDatabase
import com.sample.rohlik.db.ItemizationEntryDB
import com.sample.rohlik.network.ExpenseReports
import kotlinx.coroutines.*

class ItemizationListNetSyncManager(
    private val application: Application,
    database: AppDatabase,
    private val repository: ExpenseReports
) : NetSyncManager() {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var localSyncManager = LocalSyncManager(database)

    fun getLocalSyncManager() : LocalSyncManager{
        return localSyncManager
    }

    override fun localToServerSync() {
        runBlocking(coroutineScope.coroutineContext) {
            val modifiedItemsDBO = localSyncManager.getLocallyModifiedItems()
            for (dbo in modifiedItemsDBO) {
                when (dbo.state) {
                    QUEUED_FOR_CREATE.text -> create(dbo)
                    QUEUED_FOR_UPDATE.text -> update(dbo)
                    QUEUED_FOR_DELETE.text -> delete(dbo)
                }
            }
        }
    }

    override fun serverToLocalSync() {
        runBlocking(coroutineScope.coroutineContext) {

            launch {
                val dbItems: MutableList<ItemizationEntryDB> = mutableListOf()
                val response = withContext(Dispatchers.IO) {
                    repository.getItemizationEntries("test", "test", application)
                }
                if (response is NetworkResponse.Success) {
                    response.body.forEach { dto ->
                        dbItems.add(mapDTOtoDBO(dto))
                        Log.d("TAG", " Network response success : ")
                    }
                    localSyncManager.saveAll(dbItems)
                } else {
                    // TODO
                    Log.d("TAG", " Network response failed : ")
                }
            }
        }
    }

    private fun create(dbo: ItemizationEntryDB) {
        //TODO call create in the endpoint, manage http codes etc
    }

    private fun update(dbo: ItemizationEntryDB) {
        //TODO call update in the endpoint, manage http codes etc
    }

    private fun delete(dbo: ItemizationEntryDB) {
        //TODO call update in the endpoint, manage http codes etc
    }
}
