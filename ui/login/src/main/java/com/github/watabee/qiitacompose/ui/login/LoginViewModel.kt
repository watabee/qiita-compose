package com.github.watabee.qiitacompose.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
internal class LoginViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val requestAccessTokens = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    private val _uiState = MutableStateFlow(LoginUiState.initialValue())
    val uiState: Flow<LoginUiState> = _uiState.asStateFlow()

    private val _outputEvent = MutableSharedFlow<LoginOutputEvent>()
    val outputEvent: Flow<LoginOutputEvent> = _outputEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            requestAccessTokens.collectLatest { code ->
                _uiState.value = LoginUiState(isRequesting = true)
                when (val result = qiitaRepository.requestAccessTokens(code)) {
                    is QiitaApiResult.Success -> {
                        _uiState.value = LoginUiState(isRequesting = false)
                        userDataStore.updateAccessToken(result.response.response.token)
                        _outputEvent.emit(LoginOutputEvent.SuccessLogin)
                    }
                    is QiitaApiResult.Failure -> {
                        _uiState.value = LoginUiState(isRequesting = false)
                        _outputEvent.emit(LoginOutputEvent.FailureLogin(code))
                    }
                }
            }
        }
    }

    fun requestEvent(event: LoginInputEvent) {
        when (event) {
            is LoginInputEvent.RequestAccessTokens -> {
                requestAccessTokens.tryEmit(event.code)
            }
        }
    }
}

internal data class LoginUiState(
    val isRequesting: Boolean
) {
    companion object {
        fun initialValue() = LoginUiState(isRequesting = false)
    }
}

// View -> ViewModel
internal sealed class LoginInputEvent {
    class RequestAccessTokens(val code: String): LoginInputEvent()
}

// ViewModel -> View
internal sealed class LoginOutputEvent {
    object SuccessLogin : LoginOutputEvent()

    data class FailureLogin(val code: String) : LoginOutputEvent()
}
