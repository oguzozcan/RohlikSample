package com.sample.rohlik.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.rohlik.compose.*
import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.data.NetworkResponse
import com.sample.rohlik.network.ExpenseReportsImpl
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val REPORT_ID: String = "REPORT_ID"
const val EXPENSE_ID: String = "EXPENSE_ID"
const val PARENT_ENTRY_ID: String = "PARENT_ENTRY_ID"
const val TRANSACTION_TOTAL: String = "TRANSACTION_TOTAL"

open class ItemizationListViewModel(application: Application) : AndroidViewModel(application) {

    //TODO internal val connectivityChecker: InternetConnectivityChecker,
    internal var itemizationEntries = MutableLiveData<List<ItemizationEntryDTO>>(listOf())
    internal val selectedItemizationEntries = MutableLiveData(mutableListOf<String>())
    internal val resultType = MutableLiveData<ResultTypeItemization>(IN_PROGRESS())
    internal val selectedItemsResultType = MutableLiveData(SUCCESS())
    internal var reportId: String = "test"
    internal var parentExpenseId: String = "test"
    internal var totalTransactionAmount: Double = 0.0
    internal var parentEntryId: String = ""
    internal var itemizedAmount = 0.0
    internal var currencyCode = ""
    internal var isInMultiSelectionMode = MutableLiveData(false)

    fun setIds(extras: Bundle?) {
        cleanViewModel()
        extras?.run {
            reportId = getString(REPORT_ID).orEmpty()
            parentExpenseId = getString(EXPENSE_ID).orEmpty()
            parentEntryId = getString(PARENT_ENTRY_ID).orEmpty()
            totalTransactionAmount = getDouble(TRANSACTION_TOTAL)
        }
        itemizationEntries.postValue(listOf())
        if (isInMultiSelectionMode.value == true) {
            switchMultiSelectionMode()
        }
    }

    fun cleanViewModel() {
        reportId = ""
        parentExpenseId = ""
        totalTransactionAmount = 0.0
        itemizedAmount = 0.0
        currencyCode = ""
        parentEntryId = ""
        isInMultiSelectionMode = MutableLiveData(false)
        itemizationEntries = MutableLiveData(listOf())
    }

    fun calculateItemizationAmount(): String {
        itemizedAmount = 0.0
        itemizationEntries.value?.listIterator()?.forEach {
            itemizedAmount += it.transactionAmount.value
            currencyCode = it.transactionAmount.currencyCode
        }
        return formatAmount(itemizedAmount, Locale.getDefault(), currencyCode)
    }

    fun calculateRemainingAmount(): String {
        return formatAmount(
            (totalTransactionAmount - itemizedAmount),
            Locale.getDefault(),
            currencyCode
        )
    }

    internal fun getItemizationList() {
        if (!isOnline()) {
            return
        }
        resultType.postValue(IN_PROGRESS())
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                ExpenseReportsImpl().getItemizationEntries(
                    reportId = reportId,
                    expenseId = parentExpenseId, getApplication<Application>()
                )
            }
            processItemizationEntriesResponse(response)
        }
    }

    internal fun processItemizationEntriesResponse(response: NetworkResponse<List<ItemizationEntryDTO>, Unit>) {
        when (response) {
            is NetworkResponse.Success -> {
                resultType.postValue(SUCCESS())
                itemizationEntries.postValue(response.body)
                makeProgressBarInvisible()
            }
            is NetworkResponse.Empty, is NetworkResponse.ClientError -> resultType.postValue(
                NO_RESULT()
            )
            is NetworkResponse.ServerError -> resultType.postValue(SERVER_ERROR())
            is NetworkResponse.NetworkError -> resultType.postValue(NETWORK_ERROR())
            else -> {
                resultType.postValue(UNKNOWN_ERROR())
            }
        }
        //sendEvent(response is NetworkResponse.Success)
    }


    private fun makeProgressBarInvisible(delayMillis: Long = 500) {
        Handler(Looper.getMainLooper()).postDelayed({
            resultType.postValue(INIT_LOAD())
        }, delayMillis)
    }

    fun isOnline(): Boolean {
        //TODO not implemented yet
        return false
    }

    fun isMutationEnabled(): Boolean {
        if (isInMultiSelectionMode.value == null) {
            return false
        }
        return !isInMultiSelectionMode.value!!
    }

    fun deleteEntry(expenseId: String) {
        resultType.postValue(DELETION_IN_PROGRESS())
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                //reportEntryRepository.deleteExpense(reportId, expenseId)
            }
            //processDeleteAction(response, expenseId)
        }
    }

    fun deleteMultipleEntries() {
        resultType.postValue(DELETION_IN_PROGRESS())
        viewModelScope.launch {
            val selectedItems = selectedItemizationEntries.value?.let {
                getSelectedItems(it)
            }
            if (selectedItems.isNullOrEmpty()) {
                return@launch
            }
            for ((index, item) in selectedItems.withIndex()) {
                val response = withContext(Dispatchers.IO) {
                    //reportEntryRepository.deleteExpense(reportId, item.expenseId)
                }
                //processDeleteAction(response, item.expenseId, index + 1, selectedItems.size)
            }
        }
    }

    internal fun getSelectedItems(ids: List<String>): List<ItemizationEntryDTO> {
        val selectedItems: MutableList<ItemizationEntryDTO> = mutableListOf()
        itemizationEntries.value?.forEach { item ->
            for (id in ids) {
                if (id == item.expenseId) {
                    selectedItems.add(item)
                }
            }
        }
        return selectedItems
    }

    internal fun processDeleteAction(
        response: Single<Boolean>,
        expenseId: String,
        callCount: Int = 1,
        itemCount: Int = 1
    ) {
        response.subscribe({
            if (it == true) {
                val values = itemizationEntries.value
                values?.forEach { item ->
                    if (expenseId == item.expenseId) {
                        itemizationEntries.postValue(values.minus(item))
                    }
                }
            }
            postDeletionResult(it, callCount, itemCount)
        }, {
            postDeletionResult(false, callCount, itemCount)
        })
    }

    private fun postDeletionResult(isSuccess: Boolean, callCount: Int = 1, itemCount: Int = 1) {
        if (callCount != itemCount) {
            return
        }
        if (isSuccess) {
            resultType.postValue(DELETION_SUCCESSFUL())
        } else {
            resultType.postValue(DELETION_FAILED())
        }
        makeProgressBarInvisible(700)
    }

    fun createItemizationItem(activity: Activity, id: String) {
//        CreateItemizationActivity.startActivity(
//            reportId,
//            reportCurrencyCode,
//            activity,
//            parentExpenseId,
//            itemizedAmount,
//            totalTransactionAmount,
//        )
    }

    fun openItemizationDetails(activity: Activity, id: String) {
//        ItemizationDetailsActivity.startActivity(
//            entryId = id,
//            context = activity,
//            startActivityCode = null,
//            currencyCode = reportCurrencyCode,
//            itemizationTotal = itemizedAmount,
//            parentExpenseType = parentExpenseType
//        )

    }

    fun getMap(): Map<String, List<ItemizationEntryDTO>> {
        return itemizationEntries.value?.sortedByDescending { it.transactionDate }
            ?.groupBy { it.transactionDate?.getFormattedTime() ?: "today" } ?: emptyMap()
    }

    fun getItemCount(): Int {
        return itemizationEntries.value?.size ?: 0
    }

    fun getSelectedItemCount(): Int {
        return selectedItemizationEntries.value?.size ?: 0
    }

    fun isItemSelected(id: String): Boolean {
        return selectedItemizationEntries.value?.contains(id) ?: false
    }

    fun switchSelectItem(id: String) {
        if (isItemSelected(id)) {
            selectedItemizationEntries.value?.remove(id)
        } else {
            selectedItemizationEntries.value?.add(id)
        }
        selectedItemizationEntries.postValue(selectedItemizationEntries.value)
    }

    fun switchMultiSelectionMode() {
        isInMultiSelectionMode.postValue(!isInMultiSelectionMode.value!!)
        if (isInMultiSelectionMode.value == false)
            clearSelectedItems()
        //To refresh FAB buttons visibility
        resultType.postValue(INIT_LOAD())
        itemizationEntries.postValue(itemizationEntries.value)
    }

    fun refreshSelectedItems() {
        selectedItemsResultType.postValue(selectedItemsResultType.value)
        resultType.postValue(INIT_LOAD())
    }

    fun clearSelectedItems() {
        selectedItemizationEntries.postValue(mutableListOf())
    }

    fun selectAllItems() {
        if (isAllItemsSelected()) {
            clearSelectedItems()
        } else {
            selectedItemizationEntries.postValue(itemizationEntries.value?.map { it.expenseId }
                ?.toMutableList())
        }
        refreshSelectedItems()
    }

    private fun isAllItemsSelected() =
        selectedItemizationEntries.value?.size == itemizationEntries.value?.size && itemizationEntries.value!!.isNotEmpty()

//    fun getConnectivityObserver(): Observable<Boolean> =
//        connectivityChecker.observeInternetConnectivity()
}


