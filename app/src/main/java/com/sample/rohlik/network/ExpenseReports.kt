package com.sample.rohlik.network

import android.app.Application
import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.data.NetworkResponse

interface ExpenseReports {
    // Third param should not be here
    suspend fun getItemizationEntries(
        reportId: String,
        expenseId: String,
        app: Application
    ): NetworkResponse<List<ItemizationEntryDTO>, Unit>

}