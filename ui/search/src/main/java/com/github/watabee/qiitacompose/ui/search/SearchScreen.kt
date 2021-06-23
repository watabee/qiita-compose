package com.github.watabee.qiitacompose.ui.search

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import timber.log.Timber

@Composable
fun SearchScreen(closeSearchScreen: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var searchFocus by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = query,
            onQueryChanged = { query = it },
            searchFocus = searchFocus,
            onSearchFocusChanged = { searchFocus = it },
            onClearQuery = { query = "" },
            onSearch = { Timber.e("onSearch: $it") },
            onNavIconClicked = closeSearchScreen
        )
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
                val keyboardController = LocalSoftwareKeyboardController.current
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.typography.body1.color.copy(alpha = ContentAlpha.high)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(query)
                            keyboardController?.hide()
                        }
                    ),
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
