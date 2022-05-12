package com.sample.rohlik.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sample.rohlik.R
import com.sample.rohlik.compose.*

class ItemizationListActivity : AppCompatActivity() {

    internal val viewModel by viewModels<ItemizationListViewModel>()
    private var currentMenuResource = R.menu.itemization_edit_menu
    private var edit: MenuItem? = null

//    val registerForActivityResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                it.data?.extras?.run {
//                    val key = getString(Constants.EXTRA_TYPE_KEY, "")
//                    val code = getString(Constants.EXTRA_TYPE_EXPENSE_CODE, "")
//                    viewModel.createItemizationItem(this@ItemizationListActivity, key, code)
//                }
//            }
//        }

//    override fun onResume() {
//        super.onResume()
//        ProcessItemizationListResult(NO_RESULT)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContent {
            NoItemization{ onFabClick() }
            //OfflineBanner(viewModel.getConnectivityObserver())
        }
        viewModel.run {
            setIds(intent.extras)
            getItemizationList(supportFragmentManager)
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
                edit?.isVisible = viewModel.getItemCount() != 0 && !viewModel.isApprovalMode
            }
            R.menu.itemization_delete_all_menu -> {
                delete = menu.findItem(R.id.delete_all_items)
                delete?.isVisible = viewModel.selectedItemizationEntries.value!!.isNotEmpty() && !viewModel.isApprovalMode
                menu.findItem(R.id.itemization_select_all)?.isVisible = !viewModel.isApprovalMode
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
//                if (!viewModel.isOfflineMessageShown(supportFragmentManager)) {
//                    showTwoButtonDialog(
//                        titleResId = R.string.delete_expense,
//                        messageResId = R.string.itemization_confirm_deletion_on_selected,
//                        leftButtonTextResId = R.string.cancel,
//                        rightButtonTextResId = R.string.delete,
//                        rightButtonAction = {
//                            viewModel.switchMultiSelectionMode()
//                            viewModel.deleteMultipleEntries()
//                        })
//                }
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
            is NETWORK_ERROR, is SERVER_ERROR, is UNKNOWN_ERROR -> {
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
            NoItemization{ onFabClick() }
        } else {
            //Enable editing option only if we have items
            edit?.isVisible = true
            List(viewModel,
                { onItemClick(it) },
                { onItemLongClick(it) },
                { onFabClick() },
                { itemSwiped(it) },
                { viewModel.getItemizationList(supportFragmentManager) })
        }
        if (resultType !is INIT_LOAD) {
            ProgressBar(loadingTitle = stringResource(id = resultType.messageResource))
        }
    }

    private fun onItemClick(expenseId: String) {
        if (viewModel.isInMultiSelectionMode.value == false) {
            if (viewModel.isOfflineMessageShown(supportFragmentManager)) {
                return
            }
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
        if (viewModel.isOfflineMessageShown(supportFragmentManager)) {
            return
        }
        if (viewModel.resultType.value == DELETION_IN_PROGRESS()) {
            return
        }
//        showTwoButtonDialog(
//            titleResId = R.string.delete_expense,
//            messageResId = R.string.report_delete_expense_message,
//            leftButtonTextResId = R.string.cancel,
//            rightButtonTextResId = R.string.delete,
//            rightButtonAction = {
//                viewModel.deleteEntry(expenseId)
//            })
    }

    private fun onFabClick() {
        if (!viewModel.isOfflineMessageShown(supportFragmentManager)) {
            // show a warning if we have some allocations and also no existing itemization. i.e. creating a first one
            if (viewModel.hasAllocations && viewModel.getItemCount() == 0) {
//                showTwoButtonDialog(
//                    titleResId = R.string.general_description_notice,
//                    messageResId = R.string.general_description_itemization_allocation,
//                    leftButtonTextResId = R.string.no,
//                    rightButtonTextResId = R.string.yes,
//                    rightButtonAction = {
//                        registerForActivityResult.launch(viewModel.searchForExpenseType(this))
//                    }
//                )
            }
//            else {
//                registerForActivityResult.launch(viewModel.searchForExpenseType(this))
//            }
        }
    }

    private fun showTwoButtonDialog(
        @StringRes titleResId: Int = -1,
        @StringRes messageResId: Int = -1,
        @StringRes leftButtonTextResId: Int = -1,
        leftButtonAction: () -> Unit = {},
        @StringRes rightButtonTextResId: Int = -1,
        rightButtonAction: () -> Unit = {},
        cancelAction: (() -> Unit)? = null
    ) {
//
//        val currentAlertDialogFragment = AlertDialogUtil.createTwoButtonDialog(
//            titleResId = titleResId,
//            messageResId = messageResId, leftButtonTextResId = leftButtonTextResId,
//            leftButtonAction = leftButtonAction, rightButtonTextResId = rightButtonTextResId,
//            rightButtonAction = rightButtonAction, cancelAction = cancelAction
//        )
//        currentAlertDialogFragment?.show(supportFragmentManager, "")
    }
}