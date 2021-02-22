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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.watabee.qiitacompose.ui.common.AppDialogFragment
import com.github.watabee.qiitacompose.ui.common.DialogEvent
import com.github.watabee.qiitacompose.ui.common.setOnAppDialogFragmentEventListener
import com.github.watabee.qiitacompose.ui.util.launchWhenResumed
import com.github.watabee.qiitacompose.util.Env
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

private const val CALLBACK_URL = "qiita-compose://auth/oauth2callback"

private const val SUCCESS_LOGIN_DIALOG_TAG = "success_login_dialog"
private const val FAILURE_LOGIN_DIALOG_TAG = "failure_login_dialog"
private const val AUTH_ERROR_DIALOG_TAG = "auth_error_dialog"

private const val CODE = "code"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject lateinit var env: Env

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnAppDialogFragmentEventListener(this::handleDialogEvent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = UUID.randomUUID().toString().replace("-", "")

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        val webView: WebView = view.findViewById(R.id.web_view)
        val progressIndicator: LinearProgressIndicator = view.findViewById(R.id.progress_indicator)

        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = createWebViewClient(state, progressIndicator, viewLifecycleOwner)
        webView.webChromeClient = createWebChromeClient(progressIndicator)

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
        viewModel.uiState
            .onEach { loadingView.isVisible = it.isRequesting }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.outputEvent
            .onEach { event ->
                when (event) {
                    LoginOutputEvent.SuccessLogin -> showSuccessLoginDialog()
                    is LoginOutputEvent.FailureLogin -> showFailureLoginDialog(event.code)
                }
            }
            .launchWhenResumed(viewLifecycleOwner)

        webView.loadUrl(makeAuthUrl(state))
    }

    private fun showAuthErrorDialog() {
        AppDialogFragment.Builder()
            .message(getString(R.string.login_error_auth))
            .positiveButtonTitle(getString(android.R.string.ok))
            .show(parentFragmentManager, AUTH_ERROR_DIALOG_TAG)
    }

    private fun showSuccessLoginDialog() {
        AppDialogFragment.Builder()
            .message(getString(R.string.login_success_login))
            .positiveButtonTitle(getString(android.R.string.ok))
            .show(parentFragmentManager, SUCCESS_LOGIN_DIALOG_TAG)
    }

    private fun showFailureLoginDialog(code: String) {
        AppDialogFragment.Builder()
            .message(getString(R.string.login_failure_login))
            .positiveButtonTitle(getString(R.string.common_yes))
            .negativeButtonTitle(getString(R.string.common_no))
            .extraParams(bundleOf(CODE to code))
            .show(parentFragmentManager, FAILURE_LOGIN_DIALOG_TAG)
    }

    private fun handleDialogEvent(tag: String?, event: DialogEvent, extraParams: Bundle?) {
        when (tag) {
            SUCCESS_LOGIN_DIALOG_TAG -> {
                when (event) {
                    DialogEvent.POSITIVE_BUTTON_CLICKED -> parentFragmentManager.popBackStack()
                    DialogEvent.CANCELED -> parentFragmentManager.popBackStack()
                    else -> throw IllegalStateException()
                }
            }
            FAILURE_LOGIN_DIALOG_TAG -> {
                when (event) {
                    DialogEvent.POSITIVE_BUTTON_CLICKED -> {
                        val code = extraParams?.getString(CODE) ?: throw IllegalStateException("'code' must not be null.")
                        viewModel.requestEvent(LoginInputEvent.RequestAccessTokens(code))
                    }
                    DialogEvent.NEGATIVE_BUTTON_CLICKED -> parentFragmentManager.popBackStack()
                    DialogEvent.CANCELED -> parentFragmentManager.popBackStack()
                }
            }
            AUTH_ERROR_DIALOG_TAG -> {
                when (event) {
                    DialogEvent.POSITIVE_BUTTON_CLICKED -> parentFragmentManager.popBackStack()
                    DialogEvent.CANCELED -> parentFragmentManager.popBackStack()
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    private fun makeAuthUrl(state: String): String {
        val scope = "read_qiita+write_qiita_team"
        return "https://qiita.com/api/v2/oauth/authorize?client_id=${env.qiitaClientId}&scope=$scope&state=$state"
    }

    private fun createWebChromeClient(progressIndicator: LinearProgressIndicator): WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressIndicator.progress = newProgress
        }
    }

    private fun createWebViewClient(
        state: String,
        progressIndicator: LinearProgressIndicator,
        viewLifecycleOwner: LifecycleOwner
    ): WebViewClient =
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
                progressIndicator.show()
                dismissSnackbar()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressIndicator.hide()
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

                viewModel.requestEvent(LoginInputEvent.RequestAccessTokens(code))
                return true
            }

            fun dismissSnackbar() {
                snackbar?.dismiss()
            }
        }
}
