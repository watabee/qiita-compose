package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

internal val LocalItemsRouting = compositionLocalOf<ItemsRouting> {
    error("CompositionLocal LocalNavHostController not present")
}

@Composable
fun ItemsScreen(itemsRouting: ItemsRouting) {
    val viewModel: ItemsViewModel = hiltNavGraphViewModel()
    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val isError = lazyPagingItems.loadState.refresh is LoadState.Error

    CompositionLocalProvider(LocalItemsRouting provides itemsRouting) {
        Surface(modifier = Modifier.fillMaxSize()) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
                onRefresh = { lazyPagingItems.refresh() },
                indicator = { state, refreshTrigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTrigger,
                        contentColor = MaterialTheme.colors.primary
                    )
                }
            ) {
                if (isError) {
                    ErrorScreen(onRetryButtonClicked = { lazyPagingItems.retry() })
                } else {
                    ItemsList(lazyPagingItems = lazyPagingItems)
                }
            }
        }
    }
}

@Composable
private fun ItemsList(lazyPagingItems: LazyPagingItems<Item>) {
    LazyColumn {
        items(lazyPagingItems) {
            it?.let { item -> ItemListItem(item = item) }
        }

        when (lazyPagingItems.loadState.append) {
            LoadState.Loading -> {
                item {
                    LoadingListItem()
                }
            }
            is LoadState.Error -> {
                item {
                    ErrorListItem()
                }
            }
            is LoadState.NotLoading -> {
                // do nothing.
            }
        }
    }
}

@Composable
private fun LoadingListItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp)
            .wrapContentSize()
    )
}

@Composable
private fun ErrorListItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp)
    ) {
    }
}
