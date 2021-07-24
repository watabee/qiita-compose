package com.github.watabee.qiitacompose.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.request.SortTag
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.ui.items.ItemKey
import com.github.watabee.qiitacompose.ui.items.ItemsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val qiitaRepository: QiitaRepository) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow<String?>(null)
    val itemsFlow = queryFlow.filterNotNull()
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
                initialKey = ItemKey(page = 1, query = query),
                pagingSourceFactory = { ItemsPagingSource(qiitaRepository) }
            ).flow
        }.cachedIn(viewModelScope)

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
            is Action.SearchByQuery -> {
                queryFlow.tryEmit(action.query)
            }
        }
    }

    data class State(
        val tags: List<Tag> = emptyList()
    )

    sealed interface Action {
        object GetTags : Action

        data class SearchByQuery(val query: String) : Action
    }
}
