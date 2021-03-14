package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.common.navViewModel

internal val LocalItemsRouting = compositionLocalOf<ItemsRouting> {
    error("CompositionLocal LocalNavHostController not present")
}

@Composable
fun ItemsScreen(itemsRouting: ItemsRouting) {
    val viewModel: ItemsViewModel = navViewModel()
    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()

    CompositionLocalProvider(LocalItemsRouting provides itemsRouting) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (lazyPagingItems.loadState.refresh) {
                LoadState.Loading -> {
                    LoadingScreen()
                }
                is LoadState.Error -> {
                    ErrorScreen(onRetryButtonClicked = { lazyPagingItems.retry() })
                }
                is LoadState.NotLoading -> {
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
