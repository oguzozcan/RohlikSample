package com.sample.rohlik.synchronization

import android.app.Application
import com.sample.rohlik.data.NetworkResponse
import com.sample.rohlik.db.ItemizationEntryDB
import com.sample.rohlik.network.ExpenseReports
import kotlinx.coroutines.*

class ItemizationListNetSyncManager(
    private val application: Application,
    private val repository: ExpenseReports
) : NetSyncManager() {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var localSyncManager = LocalSyncManager(application)

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
            val dbItems: MutableList<ItemizationEntryDB> = mutableListOf()
            launch {
                val response = withContext(Dispatchers.IO) {
                    repository.getItemizationEntries("test", "test", application)
                }
                if (response is NetworkResponse.Success) {
                    response.body.forEach { dto ->
                        dbItems.add(mapDTOtoDBO(dto))
                    }
                    localSyncManager.saveAll(dbItems)
                } else {
                    // TODO
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
