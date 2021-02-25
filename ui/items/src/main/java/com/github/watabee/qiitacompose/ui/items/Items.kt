package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items

@Composable
fun Items() {
    val viewModel: ItemsViewModel = viewModel()
    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (lazyPagingItems.loadState.refresh) {
            LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }
            is LoadState.Error -> {}
            is LoadState.NotLoading -> {
                LazyColumn {
                    items(lazyPagingItems) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp)
                        ) {
                            Text(text = it?.title.orEmpty())
                        }
                    }

                    when (lazyPagingItems.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                        .wrapContentWidth()
                                        .requiredHeight(64.dp)
                                )
                            }
                        }
                        is LoadState.Error -> {
                        }
                        is LoadState.NotLoading -> {
                            // do nothing.
                        }
                    }
                }
            }
        }
    }
}
