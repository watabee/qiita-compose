package com.github.watabee.qiitacompose.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    qiitaRepository: QiitaRepository,
    dataStore: UserDataStore
) : ViewModel() {

    private val actionGetAuthenticatedUserFlow =
        MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val actionLogoutFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val getAuthenticatedUserFlow = flow {
        emit(GetAuthenticatedUserState.Loading)
        when (val result = qiitaRepository.fetchAuthenticatedUser()) {
            is QiitaApiResult.Success -> {
                emit(GetAuthenticatedUserState.Success(result.response.response))
            }
            is QiitaApiResult.Failure -> {
                emit(GetAuthenticatedUserState.Error)
            }
        }
    }

    val isLoggedIn: StateFlow<Boolean> = dataStore.accessTokenFlow
        .map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    val authenticatedUserState: StateFlow<GetAuthenticatedUserState> =
        actionGetAuthenticatedUserFlow.flatMapLatest { getAuthenticatedUserFlow }
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = GetAuthenticatedUserState.Loading)

    init {
        isLoggedIn
            .scan(false) { wasLoggedIn, isLoggedIn ->
                if (!wasLoggedIn && isLoggedIn) {
                    actionGetAuthenticatedUserFlow.tryEmit(Unit)
                }
                isLoggedIn
            }
            .launchIn(viewModelScope)

        actionLogoutFlow.onEach { dataStore.updateAccessToken(null) }
            .launchIn(viewModelScope)
    }

    fun logout() {
        actionLogoutFlow.tryEmit(Unit)
    }
}

internal sealed class GetAuthenticatedUserState {
    object Loading : GetAuthenticatedUserState()

    data class Success(val user: AuthenticatedUser) : GetAuthenticatedUserState()

    object Error : GetAuthenticatedUserState()
}
