package com.github.watabee.qiitacompose.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
internal class LoginViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userDataStore: UserDataStore
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
                _uiState.update { uiState ->
                    uiState.copy(events = uiState.events + LoginEvent.ShowLoadWebErrorSnackbarEvent())
                }
            }
        }
    }

    fun onEventDone(eventId: Long) {
        _uiState.update { uiState ->
            uiState.copy(events = uiState.events.filterNot { it.id == eventId })
        }
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
                            uiState.copy(
                                screenContent = LoginUiState.ScreenContent.EMPTY,
                                events = uiState.events + LoginEvent.ShowSuccessLoginSnackbarEvent()
                            )
                        }
                    }
                    is QiitaApiResult.Failure -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                screenContent = LoginUiState.ScreenContent.EMPTY,
                                events = uiState.events + LoginEvent.ShowFailureLoginSnackbarEvent(code)
                            )
                        }
                    }
                }
            }
            is QiitaApiResult.Failure -> {
                _uiState.update { uiState ->
                    uiState.copy(
                        screenContent = LoginUiState.ScreenContent.EMPTY,
                        events = uiState.events + LoginEvent.ShowFailureLoginSnackbarEvent(code)
                    )
                }
            }
        }
    }
}

internal data class LoginUiState(
    val screenContent: ScreenContent = ScreenContent.WEBVIEW,
    val events: List<LoginEvent> = emptyList()
) {
    enum class ScreenContent {
        EMPTY, LOADING, WEBVIEW
    }
}

// View -> ViewModel
internal sealed interface LoginAction {
    class RequestAccessTokens(val code: String) : LoginAction

    object ShowLoadWebErrorSnackbar : LoginAction
}

internal sealed class LoginEvent {
    val id: Long = UUID.randomUUID().mostSignificantBits

    class ShowLoadWebErrorSnackbarEvent : LoginEvent() {
        val messageResId: Int @StringRes get() = R.string.login_error_loading_web
        val actionLabelResId: Int @StringRes get() = R.string.login_retry
    }

    class ShowSuccessLoginSnackbarEvent : LoginEvent() {
        val messageResId: Int @StringRes get() = R.string.login_success_login
        val actionLabelResId: Int @StringRes get() = android.R.string.ok
    }

    data class ShowFailureLoginSnackbarEvent(val code: String) : LoginEvent() {
        val messageResId: Int @StringRes get() = R.string.login_failure_login
        val actionLabelResId: Int @StringRes get() = R.string.login_retry
    }
}
