package com.github.watabee.qiitacompose.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.data.UserData
import com.github.watabee.qiitacompose.datastore.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(dataStore: UserDataStore) : ViewModel() {

    private val actionLogoutFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val userData: StateFlow<UserData?> = dataStore.userDataFlow
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    init {
        actionLogoutFlow.onEach { dataStore.clear() }
            .launchIn(viewModelScope)
    }

    fun logout() {
        actionLogoutFlow.tryEmit(Unit)
    }
}
