package com.github.watabee.qiitacompose.ui.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.watabee.qiitacompose.util.Env
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

private const val CALLBACK_URL = "qiita-compose://auth/oauth2callback"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject lateinit var env: Env

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = UUID.randomUUID().toString().replace("-", "")

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        val webView: WebView = view.findViewById(R.id.web_view)
        val progressBar: ProgressBar = view.findViewById(R.id.web_progress_bar)

        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = createWebViewClient(state, progressBar, viewLifecycleOwner)
        webView.webChromeClient = createWebChromeClient(progressBar)

        requireActivity().onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (webView.canGoBack()) {
                            webView.goBack()
                        } else {
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            )

        val loadingView: View = view.findViewById(R.id.loading_view)
        viewModel.isRequesting
            .onEach { loadingView.isVisible = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.uiEvent
            .onEach { event ->
                when (event) {
                    LoginUiEvent.SuccessLogin -> showSuccessLoginDialog()
                    is LoginUiEvent.FailureLogin -> showFailureLoginDialog(event.code)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        webView.loadUrl(makeAuthUrl(state))
    }

    private fun showAuthErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.login_error_auth)
            .setPositiveButton(android.R.string.ok) { _, _ -> parentFragmentManager.popBackStack() }
            .setOnCancelListener { parentFragmentManager.popBackStack() }
            .show()
    }

    private fun showSuccessLoginDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.login_success_login)
            .setPositiveButton(android.R.string.ok) { _, _ -> parentFragmentManager.popBackStack() }
            .setOnCancelListener { parentFragmentManager.popBackStack() }
            .show()
    }

    private fun showFailureLoginDialog(code: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.login_failure_login)
            .setPositiveButton(R.string.common_yes) { _, _ -> viewModel.requestAccessTokens(code) }
            .setNegativeButton(R.string.common_no) { _, _ -> parentFragmentManager.popBackStack() }
            .setOnCancelListener { parentFragmentManager.popBackStack() }
            .show()
    }

    private fun makeAuthUrl(state: String): String {
        val scope = "read_qiita+write_qiita_team"
        return "https://qiita.com/api/v2/oauth/authorize?client_id=${env.qiitaClientId}&scope=$scope&state=$state"
    }

    private fun createWebChromeClient(progressBar: ProgressBar): WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }

    private fun createWebViewClient(state: String, progressBar: ProgressBar, viewLifecycleOwner: LifecycleOwner): WebViewClient =
        object : WebViewClient() {
            private var snackbar: Snackbar? = null

            init {
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            dismissSnackbar()
                        }
                    }
                })
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.isVisible = true
                dismissSnackbar()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.isVisible = false
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)

                val snackbar = Snackbar.make(view, R.string.login_error_loading_web, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.login_retry) { view.loadUrl(makeAuthUrl(state)) }
                snackbar.show()

                this.snackbar = snackbar
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri: Uri = request.url

                if (!uri.toString().startsWith(CALLBACK_URL)) {
                    return false
                }

                val stateQueryParam = uri.getQueryParameter("state")
                if (stateQueryParam.isNullOrBlank() || stateQueryParam != state) {
                    showAuthErrorDialog()
                    return true
                }

                val code = uri.getQueryParameter("code")
                if (code.isNullOrBlank()) {
                    showAuthErrorDialog()
                    return true
                }

                viewModel.requestAccessTokens(code)
                return true
            }

            fun dismissSnackbar() {
                snackbar?.dismiss()
            }
        }
}
