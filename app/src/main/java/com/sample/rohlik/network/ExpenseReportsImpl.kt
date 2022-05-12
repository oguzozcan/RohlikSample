package com.sample.rohlik.network

import android.app.Application
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.data.NetworkResponse

class ExpenseReportsImpl : ExpenseReports {

    override suspend fun getItemizationEntries(
        reportId: String,
        expenseId: String,
        app: Application
    ): NetworkResponse<List<ItemizationEntryDTO>, Unit> {
        // TODO (not defined since no real network calls are being done)
        // Ideally ExpenseReports should be bind to this implementation through module definition so all libraries could
        // have their own implementation of the defined interface contract,
        // And there should be no connection to the application - third param is just temporarily
        return NetworkResponse.Success(loadJSONFromAsset(app))
    }

    private fun AssetManager.readAssetsFile(fileName : String): String = open(fileName).bufferedReader().use{it.readText()}

    private fun <T> getList(jsonArray: String?, clazz: Class<T>?): List<T>? {
        val typeOfT = TypeToken.getParameterized(MutableList::class.java, clazz).type
        return Gson().fromJson(jsonArray, typeOfT)
    }

    private fun loadJSONFromAsset(app: Application): List<ItemizationEntryDTO> {
        return getList(
            app.assets.readAssetsFile("itemizationData.json"), ItemizationEntryDTO::class.java) ?: emptyList()
    }
}