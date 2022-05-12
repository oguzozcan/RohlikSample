package com.sample.rohlik.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sample.rohlik.R
import com.sample.rohlik.compose.*
import com.sample.rohlik.db.AppDatabase

class ItemizationListActivity : AppCompatActivity() {

    internal val viewModel by viewModels<ItemizationListViewModel>()
    private var currentMenuResource = R.menu.itemization_edit_menu
    private var edit: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(this)
        setContent {
            NoItemization { onFabClick() }
            //OfflineBanner(viewModel.getConnectivityObserver())
        }
        viewModel.initViewModel(database)
        viewModel.run {
            setIds(intent.extras)
            getItemizationList()
            resultType.observe(this@ItemizationListActivity) {
                setContent {
                    ProcessItemizationListResult(it)
                    //OfflineBanner(viewModel.getConnectivityObserver())
                }
            }
            isInMultiSelectionMode.observe(this@ItemizationListActivity) { isMultiSelectionActive ->
                onMultiSelectionModeChange(isMultiSelectionActive)
            }

            itemizationEntries.observe(this@ItemizationListActivity) {
                if (it.isEmpty() && edit?.isVisible == true) {
                    invalidateOptionsMenu()
                }
            }

            selectedItemizationEntries.observe(this@ItemizationListActivity) {
                if (isInMultiSelectionMode.value == true) {
                    this@ItemizationListActivity.updateTitle()
                    invalidateOptionsMenu()
                }
                refreshSelectedItems()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(currentMenuResource, menu)
        val delete: MenuItem?
        when (currentMenuResource) {
            R.menu.itemization_edit_menu -> {
                edit = menu.findItem(R.id.edit)
                edit?.isVisible = viewModel.getItemCount() != 0
            }
            R.menu.itemization_delete_all_menu -> {
                delete = menu.findItem(R.id.delete_all_items)
                delete?.isVisible = viewModel.selectedItemizationEntries.value!!.isNotEmpty()
                menu.findItem(R.id.itemization_select_all)?.isVisible = true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.itemization_select_all -> {
                viewModel.selectAllItems()
                invalidateOptionsMenu()
            }
            R.id.edit -> {
                viewModel.switchMultiSelectionMode()
            }
            R.id.delete_all_items -> {
                //TODO
                //show are you sure dialog
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.run {
            if (isInMultiSelectionMode.value == true) {
                switchMultiSelectionMode()
                return
            }
        }
        super.onBackPressed()
    }

    fun onMultiSelectionModeChange(isMultiSelectionModeActive: Boolean) {
        currentMenuResource = if (isMultiSelectionModeActive) {
            viewModel.refreshSelectedItems()
            updateTitle()
            R.menu.itemization_delete_all_menu
        } else {
            viewModel.clearSelectedItems()
            setTitle(R.string.itemizations)
            R.menu.itemization_edit_menu
        }
        invalidateOptionsMenu()
    }

    private fun updateTitle() {
        title = getString(
            R.string.general_no_of_selected_list_items_android, viewModel.getSelectedItemCount()
        )
    }

    @Composable
    private fun ProcessItemizationListResult(resultType: ResultTypeItemization) {
        when (resultType) {
            is NO_RESULT -> NoItemization { onFabClick() }
            is NETWORK_ERROR, is SERVER_ERROR -> {
                // TODO
            }
            else -> {
                // All other cases are successful with different messages
                PrepareItemizationList(resultType = resultType)
            }
        }
    }

    @Composable
    private fun PrepareItemizationList(resultType: ResultTypeItemization) {
        if (viewModel.itemizationEntries.value.isNullOrEmpty()) {
            NoItemization { onFabClick() }
        } else {
            //Enable editing option only if we have items
            edit?.isVisible = true
            List(viewModel,
                { onItemClick(it) },
                { onItemLongClick(it) },
                { onFabClick() },
                { itemSwiped(it) },
                { viewModel.getItemizationList() })
        }
        if (resultType !is INIT_LOAD) {
            ProgressBar(loadingTitle = stringResource(id = resultType.messageResource))
        }
    }

    private fun onItemClick(expenseId: String) {
        if (viewModel.isInMultiSelectionMode.value == false) {
            viewModel.openItemizationDetails(this, expenseId)
        } else {
            viewModel.switchSelectItem(expenseId)
            viewModel.resultType.postValue(INIT_LOAD())
        }
    }

    private fun onItemLongClick(expenseId: String) {
        if (viewModel.isMutationEnabled()) {
            viewModel.switchMultiSelectionMode()
            viewModel.switchSelectItem(expenseId)
        }
    }

    private fun itemSwiped(expenseId: String) {
        //show are you sure to delete dialog
    }

    private fun onFabClick() {

    }
}