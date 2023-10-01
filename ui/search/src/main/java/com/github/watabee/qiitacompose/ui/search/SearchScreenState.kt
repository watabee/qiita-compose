@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)

package com.github.watabee.qiitacompose.ui.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
internal class SearchScreenState(
    initialQuery: String,
    private val viewModel: SearchViewModel,
    private val keyboardController: SoftwareKeyboardController?,
    initialBackdropValue: BackdropValue = BackdropValue.Revealed,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
) {
    val lazyListState = LazyListState(initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset)
    val backdropScaffoldState = BackdropScaffoldState(initialBackdropValue)

    var query by mutableStateOf(initialQuery)

    suspend fun clearQuery() {
        query = ""
        revealBackLayerContent()
    }

    suspend fun revealBackLayerContent() {
        backdropScaffoldState.reveal()
    }

    suspend fun searchByTag(tag: String) {
        query = tag
        searchByQuery(tag)
    }

    suspend fun searchByQuery(newQuery: String) = coroutineScope {
        keyboardController?.hide()
        viewModel.dispatchAction(SearchViewModel.Action.SearchByQuery(newQuery))
        launch {
            backdropScaffoldState.conceal()
        }
        launch {
            lazyListState.scrollToItem(0)
        }
    }

    companion object {
        @Suppress("ktlint:standard:function-naming")
        fun Saver(viewModel: SearchViewModel, keyboardController: SoftwareKeyboardController?): Saver<SearchScreenState, *> = listSaver(
            save = {
                listOf(
                    it.query,
                    it.backdropScaffoldState.currentValue,
                    it.lazyListState.firstVisibleItemIndex,
                    it.lazyListState.firstVisibleItemScrollOffset,
                )
            },
            restore = {
                SearchScreenState(
                    initialQuery = it[0] as String,
                    viewModel = viewModel,
                    keyboardController = keyboardController,
                    initialBackdropValue = it[1] as BackdropValue,
                    initialFirstVisibleItemIndex = it[2] as Int,
                    initialFirstVisibleItemScrollOffset = it[3] as Int,
                )
            },
        )
    }
}

@Composable
internal fun rememberSearchScreenState(
    initialQuery: String = "",
    viewModel: SearchViewModel,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
): SearchScreenState {
    return rememberSaveable(
        viewModel,
        keyboardController,
        saver = SearchScreenState.Saver(viewModel, keyboardController),
    ) {
        SearchScreenState(initialQuery, viewModel, keyboardController)
    }
}
