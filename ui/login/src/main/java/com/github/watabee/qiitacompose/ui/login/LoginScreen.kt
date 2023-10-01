package com.github.watabee.qiitacompose.ui.login

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import java.util.UUID

private const val CALLBACK_URL = "qiita-compose://auth/oauth2callback"

@Composable
fun LoginScreen(qiitaClientId: String, navigateUp: () -> Unit) {
    val viewModel: LoginViewModel = hiltViewModel()

    val uiState: LoginUiState by viewModel.uiState.collectAsState()

    if (uiState.shouldNavigateUp) {
        navigateUp()
        viewModel.onNavigateUpDone()
    }

    Scaffold(
        modifier = Modifier.navigationBarsWithImePadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.login_toolbar_title))
                },
            )
        },
        content = { paddingValues ->
            when (uiState.screenContent) {
                LoginUiState.ScreenContent.EMPTY -> {}
                LoginUiState.ScreenContent.LOADING -> {
                    LoadingScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                LoginUiState.ScreenContent.WEBVIEW -> {
                    LoginScreen(
                        modifier = Modifier.padding(paddingValues),
                        qiitaClientId = qiitaClientId,
                        onRequestAccessToken = { viewModel.dispatchAction(LoginAction.RequestAccessTokens(it)) },
                        onLoadError = { viewModel.dispatchAction(LoginAction.ShowLoadWebErrorSnackbar) },
                    )
                }
            }
        },
    )
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    qiitaClientId: String,
    onRequestAccessToken: (code: String) -> Unit,
    onLoadError: () -> Unit,
) {
    val state = remember { createState() }
    val url = remember(state, qiitaClientId) {
        "https://qiita.com/api/v2/oauth/authorize?client_id=$qiitaClientId&scope=read_qiita+write_qiita&state=$state"
    }

    val webViewState = rememberWebViewState(url = url)
    WebView(
        state = webViewState,
        modifier = modifier.fillMaxSize(),
        onCreated = { webView ->
            @SuppressLint("SetJavaScriptEnabled")
            webView.settings.javaScriptEnabled = true
        },
    )

    val currentUrl = webViewState.lastLoadedUrl
    // TODO: handle error
    if (currentUrl?.startsWith(CALLBACK_URL) == true) {
        val uri = Uri.parse(currentUrl)
        val code = uri.getQueryParameter("code")
        if (code.isNullOrBlank() || uri.getQueryParameter("state") != state) {
            onLoadError()
        } else {
            onRequestAccessToken(code)
        }
    }
}

private fun createState(): String {
    return UUID.randomUUID().toString().replace("-", "")
}
