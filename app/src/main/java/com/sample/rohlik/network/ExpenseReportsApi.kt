package com.sample.rohlik.network

import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.data.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExpenseReportsApi {
    @GET("/expensereports/v4/users/{userId}/context/{context}/reports/{reportId}/expenses/{expenseId}/itemizations")
    suspend fun getItemizationEntries(
        @Path("userId") userId: String,
        @Path("reportId") reportId: String,
        @Path("expenseId") expenseId: String,
        @Path("context") context: String
    ): NetworkResponse<List<ItemizationEntryDTO>, Unit>
}