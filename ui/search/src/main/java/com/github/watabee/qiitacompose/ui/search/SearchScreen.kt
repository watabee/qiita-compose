package com.github.watabee.qiitacompose.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ContentAlpha
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.common.AppOutlinedButton
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.items.ItemsList
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    openUserScreen: (User) -> Unit,
    closeSearchScreen: () -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val isKeyboardVisible = LocalWindowInsets.current.ime.isVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    var query by rememberSaveable { mutableStateOf("") }
    var searchFocus by rememberSaveable { mutableStateOf(true) }

    val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val isError = lazyPagingItems.loadState.refresh is LoadState.Error

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.dispatchAction(SearchViewModel.Action.FindTags)
    }

    val search: (String) -> Unit = { query ->
        keyboardController?.hide()
        viewModel.dispatchAction(SearchViewModel.Action.SearchByQuery(query))
        coroutineScope.launch {
            lazyListState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsWithImePadding()
    ) {
        SearchBar(
            query = query,
            onQueryChanged = { query = it },
            searchFocus = searchFocus,
            onSearchFocusChanged = { searchFocus = it },
            onClearQuery = { query = "" },
            onSearch = search,
            onNavIconClicked = closeSearchScreen
        )

        when {
            isKeyboardVisible -> {
                TagsList(
                    tags = state.tags,
                    onTagClicked = {
                        query = it.id
                        search(it.id)
                    }
                )
            }
            isRefreshing -> {
                LoadingScreen()
            }
            isError -> {
                ErrorScreen(onRetryButtonClicked = { lazyPagingItems.retry() })
            }
            else -> {
                ItemsList(lazyPagingItems = lazyPagingItems, lazyListState = lazyListState, openUserScreen = openUserScreen)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    searchFocus: Boolean,
    onSearchFocusChanged: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: (String) -> Unit,
    onNavIconClicked: () -> Unit
) {
    val focusRequester = FocusRequester()

    SideEffect {
        if (searchFocus) {
            focusRequester.requestFocus()
        }
    }

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
                    .weight(1f)
                    .fillMaxHeight(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            onSearchFocusChanged(it.isFocused)
                        }
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
    if (tags.isEmpty()) {
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.requiredHeight(16.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = stringResource(id = R.string.search_tags_title), style = MaterialTheme.typography.subtitle2)
        }
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                        Image(
                            painter = rememberImagePainter(data = tag.iconUrl),
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
