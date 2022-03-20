package com.github.watabee.qiitacompose.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.common.AppOutlinedButton
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.items.ItemsList
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    openUserScreen: suspend (User) -> Unit,
    openItemDetailScreen: (String) -> Unit,
    closeSearchScreen: () -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val isError = lazyPagingItems.loadState.refresh is LoadState.Error

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.dispatchAction(SearchViewModel.Action.FindTags)
    }

    val screenState = rememberSearchScreenState(viewModel = viewModel)

    BackHandler(screenState.backdropScaffoldState.isConcealed) {
        coroutineScope.launch {
            screenState.revealBackLayerContent()
        }
    }

    if (!state.isFindingTags) {
        BackdropScaffold(
            frontLayerScrimColor = Color.Unspecified,
            scaffoldState = screenState.backdropScaffoldState,
            peekHeight = 72.dp,
            appBar = {
                SearchBar(
                    query = screenState.query,
                    onQueryChanged = { screenState.query = it },
                    onClearQuery = {
                        coroutineScope.launch {
                            screenState.clearQuery()
                        }
                    },
                    onSearch = {
                        coroutineScope.launch {
                            screenState.searchByQuery(it)
                        }
                    },
                    onNavIconClicked = closeSearchScreen
                )
            },
            backLayerContent = {
                TagsList(
                    tags = state.tags,
                    onTagClicked = { tag: Tag ->
                        coroutineScope.launch {
                            screenState.searchByTag(tag.id)
                        }
                    }
                )
            },
            frontLayerContent = {
                when {
                    isRefreshing -> {
                        LoadingScreen()
                    }
                    isError -> {
                        ErrorScreen(onRetryButtonClicked = { lazyPagingItems.retry() })
                    }
                    else -> {
                        if (lazyPagingItems.itemCount > 0) {
                            ItemsList(
                                lazyPagingItems = lazyPagingItems,
                                lazyListState = screenState.lazyListState,
                                openUserScreen = openUserScreen,
                                openItemDetailScreen = openItemDetailScreen
                            )
                        } else {
                            EmptyMessage()
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: (String) -> Unit,
    onNavIconClicked: () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        elevation = AppBarDefaults.TopAppBarElevation,
        shape = RectangleShape,
        modifier = Modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(AppBarDefaults.ContentPadding)
                .height(56.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                IconButton(onClick = onNavIconClicked) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        tint = MaterialTheme.colors.primarySurface,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                        Text(text = stringResource(id = R.string.search_hint), style = MaterialTheme.typography.body1)
                    }
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.typography.body1.color.copy(alpha = ContentAlpha.high)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                    cursorBrush = SolidColor(MaterialTheme.colors.primarySurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_cancel),
                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun TagsList(tags: List<Tag>, onTagClicked: (Tag) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.requiredHeight(16.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = stringResource(id = R.string.search_tags_title), style = MaterialTheme.typography.subtitle2)
        }
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                mainAxisAlignment = MainAxisAlignment.SpaceAround,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                tags.forEach { tag ->
                    AppOutlinedButton(onClick = { onTagClicked(tag) }) {
                        AsyncImage(
                            model = tag.iconUrl,
                            modifier = Modifier.size(16.dp),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tag.id,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage() {
    Box(
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = stringResource(id = R.string.search_empty_message))
        }
    }
}
