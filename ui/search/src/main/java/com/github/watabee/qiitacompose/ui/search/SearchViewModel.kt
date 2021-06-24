package com.github.watabee.qiitacompose.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.request.SortTag
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val qiitaRepository: QiitaRepository) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun dispatchAction(action: Action) {
        when (action) {
            Action.GetTags -> {
                viewModelScope.launch {
                    val tags = when (val result = qiitaRepository.findTags(page = 1, perPage = 20, sortTag = SortTag.COUNT)) {
                        is QiitaApiResult.Success -> result.response
                        is QiitaApiResult.Failure -> emptyList()
                    }
                    _state.value = State(tags)
                }
            }
        }
    }

    data class State(
        val tags: List<Tag> = emptyList()
    )

    sealed interface Action {
        object GetTags : Action
    }
}
