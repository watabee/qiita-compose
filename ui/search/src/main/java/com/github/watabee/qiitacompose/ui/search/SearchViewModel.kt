package com.github.watabee.qiitacompose.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.db.dao.TagDao
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.ui.items.ItemKey
import com.github.watabee.qiitacompose.ui.items.ItemsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    tagDao: TagDao,
    private val findTagsUseCase: FindTagsUseCase
) : ViewModel() {

    val state: StateFlow<State> = tagDao.getAllTags()
        .map { State(it) }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = State(emptyList()))

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
            Action.FindTags -> {
                viewModelScope.launch {
                    findTagsUseCase.execute()
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
        object FindTags : Action

        data class SearchByQuery(val query: String) : Action
    }
}
