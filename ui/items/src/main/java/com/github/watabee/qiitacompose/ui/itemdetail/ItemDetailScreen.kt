package com.github.watabee.qiitacompose.ui.itemdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.watabee.qiitacompose.ui.common.ErrorScreen

@Composable
fun ItemDetailScreen(url: String) {
    var showLoading by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var goBack: (() -> Unit)? by remember { mutableStateOf(null) }
    var showError by remember { mutableStateOf(false) }
    var reload: (() -> Unit)? by remember { mutableStateOf(null) }

    BackHandler(enabled = canGoBack, onBack = { goBack?.invoke() })

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            factory = { context ->
                val webView = WebView(context)
                goBack = { webView.goBack() }
                reload = { webView.reload() }

                @SuppressLint("SetJavaScriptEnabled")
                webView.settings.javaScriptEnabled = true
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        canGoBack = view?.canGoBack() ?: false
                        showError = false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        canGoBack = view?.canGoBack() ?: false
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        if (request?.isForMainFrame == true) {
                            showError = true
                        }
                    }
                }
                webView.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        showLoading = newProgress < 100
                    }
                }
                webView
            },
            update = { webView ->
                webView.loadUrl(url)
            },
        )

        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
            )
        }
        if (showError) {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                onRetryButtonClicked = { reload?.invoke() },
            )
        }
    }
}
