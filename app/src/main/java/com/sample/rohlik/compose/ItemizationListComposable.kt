package com.sample.rohlik.compose

import androidx.annotation.ColorRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.sample.rohlik.R
import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.ui.ItemizationListViewModel
import io.reactivex.Observable
import java.util.*
import kotlin.math.roundToInt

const val progressBar = "progressBar"

@Composable
fun NoItemization(onFabClick: () -> Unit) {
    MaterialTheme {
        Scaffold(
            backgroundColor = colorResource(R.color.hig_white),
            floatingActionButton = {
                val offset = remember {mutableStateOf(0f)}
                FAB(stringResource(id = R.string.entry_add_itemization), true, offset) {
                    onFabClick()
                }
            }, floatingActionButtonPosition = FabPosition.End
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_itemize),
                    contentDescription = stringResource(id = R.string.no_itemization),
                    Modifier.size(largeIconSize),
                    tint = colorResource(id = R.color.hig_medium_grey)
                )
                Text(
                    text = stringResource(id = R.string.itemization_empty),
                    fontSize = rowTextSize,
                    color = colorResource(id = R.color.hig_medium_grey)
                )
            }
        }
    }
}

@Preview(name = "Light theme")
@Composable
fun CircularProgressBar() {
    MaterialTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
            CircularProgressIndicator(
                modifier = Modifier.testTag(progressBar),
                color = colorResource(R.color.hig_blue)
            )
        }
    }
}

@Composable
fun ProgressBar(loadingTitle: String) {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val shape = RoundedCornerShape(progressBarRoundedCornerEdge)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(mediumIconSize)
                    .clip(shape)
                    .background(color = colorResource(id = R.color.black_overlay)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(defaultSpace)
                )
                Icon(
                    painterResource(id = R.drawable.ic_itemize),
                    contentDescription = loadingTitle,
                    Modifier
                        .size(largeIconSize)
                        .padding(defaultSpace),
                    tint = colorResource(R.color.hig_white)
                )
                Text(
                    text = loadingTitle,
                    Modifier.padding(start = defaultSpace, end = defaultSpace, bottom = defaultSpace),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.hig_white),
                    textAlign = TextAlign.Center,
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .testTag(progressBar)
                        .size(loadingBarSize)
                        .padding(bottom = defaultSpace, top = defaultSpace),
                    color = colorResource(R.color.hig_white),
                    strokeWidth = progressIndicatorStroke,
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bigSpace)
                )
            }
        }
    }
}

@Composable
fun OfflineBanner(value: Observable<Boolean>) {
    val isConnected: Boolean? by value.subscribeAsState(true)
    if (isConnected == false) {
        Row(
            Modifier
                .background(colorResource(id = R.color.hig_dark_grey))
                .fillMaxWidth()
                .padding(defaultSpace),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.offline_snackbar_message),
                color = colorResource(R.color.hig_white),
                fontSize = rowMainTextSize
            )
        }
    }
}

@Composable
fun FAB(actionName: String, isVisible: Boolean, bottomBarOffsetHeightPx: MutableState<Float>, onFabClick: () -> Unit) {
    if (!isVisible) {
        return
    }
    FloatingActionButton(
        modifier = Modifier.offset {
            IntOffset(
                x = 0,
                y = -bottomBarOffsetHeightPx.value.roundToInt()
            )
        },
        onClick = onFabClick,
        backgroundColor = colorResource(id = R.color.hig_blue),
        contentColor = colorResource(id = R.color.hig_white),
    ) {
        Icon(Icons.Filled.Add, actionName)
    }
}

@Composable
fun SummaryView(itemizedAmount: String, remainingAmount: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(id = R.color.hig_white)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FullDivider()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(mediumLinePadding)
        )
        ItemizationLine(
            title = "${stringResource(id = R.string.Itemized)} $itemizedAmount",
            fontSize = smallText,
            color = R.color.hig_charcoal,
            paddingBottom = mediumLinePadding

        )
        ItemizationLine(
            title = "${stringResource(id = R.string.itemize_remaining_label)} $remainingAmount",
            fontSize = smallText,
            color = R.color.hig_charcoal,
            fontWeight = FontWeight.Bold,
            paddingBottom = mediumLinePadding
        )
    }
}

@Composable
fun FullDivider(padding: Dp = 0.dp, color: Int = R.color.hig_light_grey) {
    Divider(
        modifier = Modifier
            .height(dividerSize)
            .fillMaxWidth()
            .padding(start = padding),
        color = colorResource(id = color)
    )
}

@Composable
fun GroupingHeader(date: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(id = R.color.hig_off_white))
            .padding(top = bigSpace),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        ItemizationLine(
            title = date,
            color = R.color.hig_concur_blue,
            fontSize = smallText,
            paddingBottom = smallSpace,
            paddingHorizontal = defaultSpace
        )
        FullDivider()
    }
}

@Composable
fun ItemizationLine(
    title: String,
    @ColorRes color: Int = R.color.hig_charcoal,
    fontSize: TextUnit = rowTextSize,
    fontWeight: FontWeight = FontWeight.Normal,
    paddingBottom: Dp,
    paddingHorizontal: Dp = 0.dp,
) {
    Text(
        text = title,
        fontSize = fontSize,
        overflow = TextOverflow.Ellipsis,
        color = colorResource(id = color),
        fontWeight = fontWeight,
        modifier = Modifier.padding(bottom = paddingBottom, start = paddingHorizontal, end = paddingHorizontal)
    )
}

@ExperimentalFoundationApi
@Composable
fun ItemizationRow(
    viewModel: ItemizationListViewModel,
    isLastItem: Boolean,
    item: ItemizationEntryDTO,
    onRowClick: (expenseId: String) -> Unit,
    onRowLongClick: (expenseId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.hig_white))
            .combinedClickable(
                onClick = { onRowClick(item.expenseId) },
                onLongClick = { onRowLongClick(item.expenseId) },
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = defaultSpace,
                    start = defaultSpace,
                    end = defaultSpace
                )
        ) {
            if (viewModel.isInMultiSelectionMode.value == true) {
                MultiSelectionColumn(viewModel = viewModel, expenseId = item.expenseId)
            }
            Column(
                modifier = Modifier.wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                ItemizationLine(
                    title = item.expenseType.name.orEmpty(),
                    fontSize = rowMainTextSize,
                    color = R.color.hig_charcoal,
                    fontWeight = FontWeight.Medium,
                    paddingBottom = mediumLinePadding
                )
                ItemizationLine(
                    title = getLongDate(item.transactionDate), //getFormattedTime(item.transactionDate),
                    fontSize = rowSubtitleSize,
                    color = R.color.hig_dark_grey,
                    paddingBottom = smallLinePadding
                )
                ItemizationLine(
                    title = item.location?.name.orEmpty(),
                    fontSize = rowSubtitleSize,
                    color = R.color.hig_dark_grey,
                    paddingBottom = mediumLinePadding
                )
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Column(
                modifier = Modifier.wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                ItemizationLine(
                    title = formatAmount(
                        item.transactionAmount.value,
                        Locale.getDefault(),
                        item.transactionAmount.currencyCode
                    ),
                    fontSize = rowTextSize,
                    color = R.color.hig_concur_blue,
                    fontWeight = FontWeight.Bold,
                    paddingBottom = defaultSpace
                )
            }
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Start) {
            FullDivider()
        }
        if (isLastItem) {
            LastItemPadding()
        }
    }
}

@Composable
fun LastItemPadding() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(bottomBarSize)
            .background(colorResource(id = R.color.hig_off_white))
    )
}

@Composable
fun MultiSelectionColumn(
    viewModel: ItemizationListViewModel,
    expenseId: String
) {
    val isSelected = remember { mutableStateOf(viewModel.isItemSelected(expenseId)) }
    // unfortunately i need to force update the value like this in case of all items selected from menu
    isSelected.value = isSelected.value
    Checkbox(
        checked = viewModel.isItemSelected(expenseId),
        onCheckedChange = {
            viewModel.switchSelectItem(expenseId)
            isSelected.value = viewModel.isItemSelected(expenseId)
        },
        colors = CheckboxDefaults.colors(colorResource(R.color.hig_blue)),
        modifier = Modifier.padding(start = defaultSpace, end = defaultSpace)
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun List(
    viewModel: ItemizationListViewModel,
    onRowClick: (expenseId: String) -> Unit,
    onRowLongClick: (expenseId: String) -> Unit,
    onFabClick: () -> Unit,
    onSwipe: (expenseId: String) -> Unit,
    onRefresh: () -> Unit
) {
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarSize.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection =
        calculateNestedScrollToHideBottomBar(bottomBarOffsetHeightPx, bottomBarHeightPx)
    MaterialTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            backgroundColor = colorResource(id = R.color.hig_white),
            floatingActionButton = {
                FAB(
                    stringResource(id = R.string.entry_add_itemization),
                    viewModel.isMutationEnabled(),
                    bottomBarOffsetHeightPx
                ) {
                    onFabClick()
                }
            }, floatingActionButtonPosition = FabPosition.End,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .height(bottomBarSize)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -bottomBarOffsetHeightPx.value.roundToInt()
                            )
                        }, backgroundColor = colorResource(id = R.color.hig_white)
                ) {
                    SummaryView(
                        itemizedAmount = viewModel.calculateItemizationAmount(),
                        remainingAmount = viewModel.calculateRemainingAmount(),
                    )
                }
            }, content = {
                ListContent(
                    viewModel,
                    onRowClick,
                    onRowLongClick,
                    onSwipe,
                    onRefresh
                )
            }
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ListContent(
    viewModel: ItemizationListViewModel,
    onRowClick: (expenseId: String) -> Unit,
    onRowLongClick: (expenseId: String) -> Unit,
    onSwipe: (expenseId: String) -> Unit,
    onRefresh: () -> Unit
) {
    val grouped = viewModel.getMap()
    val inProgress = viewModel.resultType.value is IN_PROGRESS
    SwipeRefresh(
        state = rememberSwipeRefreshState(inProgress),
        onRefresh = { onRefresh() },
        refreshTriggerDistance = swipeToRefreshDistance,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                contentColor = colorResource(id = R.color.hig_blue)
            )
        }
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.background(colorResource(R.color.hig_off_white))
        ) {
            grouped.asIterable().forEachIndexed { groupIndex, (date, groupedItems) ->
                stickyHeader {
                    GroupingHeader(date = date)
                }
                itemsIndexed(groupedItems, { _, listItem: ItemizationEntryDTO -> listItem.expenseId }) { index, item ->
                    val isLastItem =
                        (index == groupedItems.size - 1) && groupIndex == grouped.size - 1
                    if (!viewModel.isMutationEnabled() || !item.canDelete) {
                        ItemizationRow(
                            viewModel,
                            isLastItem,
                            item,
                            onRowClick,
                            onRowLongClick
                        )
                    } else {
                        SwipeLayout(
                            viewModel,
                            isLastItem,
                            item,
                            onSwipe,
                            onRowClick,
                            onRowLongClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun calculateNestedScrollToHideBottomBar(
    bottomBarOffsetHeightPx: MutableState<Float>,
    bottomBarHeightPx: Float
): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.value + delta
                bottomBarOffsetHeightPx.value = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SwipeLayout(
    viewModel: ItemizationListViewModel,
    isLastItem: Boolean = false,
    item: ItemizationEntryDTO,
    onSwipe: (expenseId: String) -> Unit,
    onRowClick: (expenseId: String) -> Unit,
    onRowLongClick: (expenseId: String) -> Unit,
) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onSwipe(item.expenseId)
        LaunchedEffect(key1 = Unit, block = {
            dismissState.snapTo(DismissValue.Default)
        })
    }
    SwipeToDismiss(state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = {
            FractionalThreshold(0.4f)
        },
        background = {
            DeleteBox(isLastItem)
        },
        dismissContent = {
            ItemizationRow(viewModel, isLastItem, item, onRowClick, onRowLongClick)
        })
}

@Composable
fun DeleteBox(isLastItem: Boolean = false) {
    Column(
        Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .background(colorResource(R.color.hig_red))
                .padding(horizontal = defaultSpace),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(
                Modifier.width(IntrinsicSize.Min), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(R.drawable.ic_delete_white_sel),
                    contentDescription = stringResource(R.string.delete_itemization),
                    modifier = Modifier
                        .size(swipeIconSize),
                    tint = colorResource(R.color.hig_white)
                )
                Text(
                    text = stringResource(R.string.remove),
                    fontSize = rowSubtitleSize,
                    color = colorResource(R.color.hig_white)
                )
            }
        }
        if (isLastItem) {
            LastItemPadding()
        }
    }
}