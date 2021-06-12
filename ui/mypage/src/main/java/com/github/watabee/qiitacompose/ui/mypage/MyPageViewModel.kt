package com.github.watabee.qiitacompose.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(State(isLoading = true))
    val state: StateFlow<State> = _state.asStateFlow()

    private val eventChannel = Channel<Event>(capacity = Channel.BUFFERED)
    val event: Flow<Event> = eventChannel.receiveAsFlow()

    override fun onCleared() {
        super.onCleared()
        kotlin.runCatching { eventChannel.close() }
    }

    fun dispatchAction(action: Action) {
        when (action) {
            Action.GetAuthenticatedUser -> {
                getAuthenticatedUser()
            }
            Action.Logout -> {
                logout()
            }
        }
    }

    private fun getAuthenticatedUser() {
        viewModelScope.launch {
            _state.value = State(isLoading = true)
            val userData = userDataStore.userDataFlow.firstOrNull()
            val accessToken = userData?.accessToken
            if (accessToken.isNullOrEmpty()) {
                eventChannel.trySend(Event.EmptyAccessToken)
            } else {
                when (val result = qiitaRepository.getAuthenticatedUser(accessToken = accessToken)) {
                    is QiitaApiResult.Success -> {
                        _state.value = State(authenticatedUser = result.response)
                    }
                    is QiitaApiResult.Failure -> {
                        _state.value = State(isError = true)
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userDataStore.clear()
            eventChannel.trySend(Event.CompletedLogout)
        }
    }

    sealed class Action {
        object GetAuthenticatedUser : Action()

        object Logout : Action()
    }

    sealed class Event {
        object EmptyAccessToken : Event()

        object CompletedLogout : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val authenticatedUser: AuthenticatedUser? = null
    )
}
