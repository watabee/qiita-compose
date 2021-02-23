package com.github.watabee.qiitacompose.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.watabee.qiitacompose.repository.QiitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ItemsViewModel @Inject constructor(qiitaRepository: QiitaRepository) : ViewModel() {
    val itemsFlow = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
        pagingSourceFactory = { ItemsPagingSource(qiitaRepository) }
    ).flow.cachedIn(viewModelScope)
}
