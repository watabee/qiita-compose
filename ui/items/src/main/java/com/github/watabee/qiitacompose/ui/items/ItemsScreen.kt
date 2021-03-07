package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.ui.common.AppOutlinedButton
import com.github.watabee.qiitacompose.ui.common.navViewModel

@Composable
fun ItemsScreen() {
    val viewModel: ItemsViewModel = navViewModel()
    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (lazyPagingItems.loadState.refresh) {
            LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is LoadState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.error),
                            modifier = Modifier.requiredSize(32.dp)
                        )
                        Spacer(modifier = Modifier.requiredWidth(8.dp))
                        Text(text = stringResource(id = R.string.common_connection_error_message), style = MaterialTheme.typography.body1)
                    }

                    Spacer(modifier = Modifier.requiredHeight(32.dp))

                    AppOutlinedButton(
                        onClick = { lazyPagingItems.retry() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.common_retry), style = MaterialTheme.typography.button)
                    }
                }
            }
            is LoadState.NotLoading -> {
                ItemsList(lazyPagingItems = lazyPagingItems)
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
