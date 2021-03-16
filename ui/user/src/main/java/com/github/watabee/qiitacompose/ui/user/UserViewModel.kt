package com.github.watabee.qiitacompose.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.util.throttleFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    dataStore: UserDataStore
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    val event: Flow<Event> = eventChannel.receiveAsFlow()

    private val actionFollowOrUnfollowUser =
        MutableSharedFlow<Action>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    val dispatchAction: (Action) -> Unit = { action ->
        when (action) {
            is Action.CheckFollowingUser -> {
                viewModelScope.launch { checkFollowingUser(action.userId) }
            }
            is Action.FollowUser, is Action.UnfollowUser -> {
                actionFollowOrUnfollowUser.tryEmit(action)
            }
        }
    }

    val isLoggedIn: StateFlow<Boolean> = dataStore.accessTokenFlow
        .map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    init {
        actionFollowOrUnfollowUser
            .onEach { action ->
                when (action) {
                    is Action.FollowUser -> followUser(action.userId)
                    is Action.UnfollowUser -> unfollowUser(action.userId)
                    else -> {
                        // do nothing.
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun checkFollowingUser(userId: String) {
        _state.emit(State.Loading)
        when (val result = qiitaRepository.isFollowingUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.emit(State.VisibleUser(result.response.response))
            }
            is QiitaApiResult.Failure -> {
                _state.emit(State.FindUserError)
            }
        }
    }

    private suspend fun followUser(userId: String) {
        when (qiitaRepository.followUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.emit(State.VisibleUser(isFollowingUser = true))
            }
            is QiitaApiResult.Failure -> {
                eventChannel.send(Event.ShowFollowUserError)
            }
        }
    }

    private suspend fun unfollowUser(userId: String) {
        when (qiitaRepository.unfollowUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.emit(State.VisibleUser(isFollowingUser = false))
            }
            is QiitaApiResult.Failure -> {
                eventChannel.send(Event.ShowUnfollowUserError)
            }
        }
    }

    sealed class State {
        object Loading : State()
        object FindUserError : State()
        data class VisibleUser(val isFollowingUser: Boolean) : State()
    }

    sealed class Event {
        object ShowFollowUserError : Event()
        object ShowUnfollowUserError : Event()
    }

    sealed class Action {
        data class CheckFollowingUser(val userId: String) : Action()

        data class FollowUser(val userId: String) : Action()

        data class UnfollowUser(val userId: String) : Action()
    }
}
