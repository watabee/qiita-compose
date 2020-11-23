package com.github.watabee.qiitacompose.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoginViewModel @ViewModelInject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val requestAccessTokens = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _isRequesting = MutableStateFlow(false)
    val isRequesting: StateFlow<Boolean> = _isRequesting.asStateFlow()

    private val _uiEvent = ConflatedBroadcastChannel<LoginUiEvent>()
    @OptIn(FlowPreview::class) val uiEvent: Flow<LoginUiEvent> get() = _uiEvent.asFlow()

    init {
        viewModelScope.launch {
            requestAccessTokens.collectLatest { code ->
                _isRequesting.value = true
                when (val result = qiitaRepository.requestAccessTokens(code)) {
                    is QiitaApiResult.Success -> {
                        _isRequesting.value = false
                        userDataStore.updateAccessToken(result.response.response.token)
                        _uiEvent.offer(LoginUiEvent.SuccessLogin)
                    }
                    is QiitaApiResult.Failure -> {
                        _isRequesting.value = false
                        _uiEvent.offer(LoginUiEvent.FailureLogin(code))
                    }
                }
            }
        }
    }

    fun requestAccessTokens(code: String) {
        requestAccessTokens.tryEmit(code)
    }
}

internal sealed class LoginUiEvent {
    object SuccessLogin : LoginUiEvent()

    data class FailureLogin(val code: String) : LoginUiEvent()
}
