package com.github.watabee.qiitacompose.ui.login

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.ui.util.Message
import com.github.watabee.qiitacompose.ui.util.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
internal class LoginViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userDataStore: UserDataStore,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var requestAccessTokenJob: Job? = null

    fun dispatchAction(event: LoginAction) {
        when (event) {
            is LoginAction.RequestAccessTokens -> {
                if (requestAccessTokenJob?.isActive != true) {
                    requestAccessTokenJob = requestAccessToken(event.code)
                }
            }
            LoginAction.ShowLoadWebErrorSnackbar -> {
                snackbarManager.showMessage(
                    messageTextId = R.string.login_error_loading_web,
                    duration = SnackbarDuration.Indefinite,
                    action = Message.Action(R.string.login_retry) {
                        navigateUp()
                    }
                )
            }
        }
    }

    private fun navigateUp() {
        _uiState.update { uiState -> uiState.copy(shouldNavigateUp = true) }
    }

    fun onNavigateUpDone() {
        _uiState.update { uiState -> uiState.copy(shouldNavigateUp = false) }
    }

    private fun requestAccessToken(code: String): Job = viewModelScope.launch {
        _uiState.update { uiState -> uiState.copy(screenContent = LoginUiState.ScreenContent.LOADING) }
        when (val getAccessTokensResult = qiitaRepository.getAccessTokens(code)) {
            is QiitaApiResult.Success -> {
                val accessToken = getAccessTokensResult.response.token
                when (val getAuthenticatedUserResult = qiitaRepository.getAuthenticatedUser(accessToken)) {
                    is QiitaApiResult.Success -> {
                        userDataStore.updateUserData(accessToken, getAuthenticatedUserResult.response.profileImageUrl)
                        _uiState.update { uiState ->
                            uiState.copy(screenContent = LoginUiState.ScreenContent.EMPTY)
                        }
                        snackbarManager.showMessage(
                            messageTextId = R.string.login_success_login,
                            duration = SnackbarDuration.Indefinite,
                            action = Message.Action(messageId = android.R.string.ok) {
                                navigateUp()
                            }
                        )
                    }
                    is QiitaApiResult.Failure -> {
                        _uiState.update { uiState ->
                            uiState.copy(screenContent = LoginUiState.ScreenContent.EMPTY)
                        }
                        showRetrySnackbar(code)
                    }
                }
            }
            is QiitaApiResult.Failure -> {
                _uiState.update { uiState ->
                    uiState.copy(screenContent = LoginUiState.ScreenContent.EMPTY)
                }
                showRetrySnackbar(code)
            }
        }
    }

    private fun showRetrySnackbar(code: String) {
        snackbarManager.showMessage(
            messageTextId = R.string.login_failure_login,
            duration = SnackbarDuration.Indefinite,
            action = Message.Action(messageId = R.string.login_retry) { result: SnackbarResult ->
                if (result == SnackbarResult.ActionPerformed) {
                    dispatchAction(LoginAction.RequestAccessTokens(code))
                }
            }
        )
    }
}

internal data class LoginUiState(
    val screenContent: ScreenContent = ScreenContent.WEBVIEW,
    val shouldNavigateUp: Boolean = false
) {
    enum class ScreenContent {
        EMPTY, LOADING, WEBVIEW
    }
}

// View -> ViewModel
internal sealed interface LoginAction {
    class RequestAccessTokens(val code: String) : LoginAction

    data object ShowLoadWebErrorSnackbar : LoginAction
}
