package com.github.watabee.qiitacompose.ui.items

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.repository.QiitaRepository
import timber.log.Timber

data class ItemKey(val page: Int, val query: String? = null) {
    fun plus(value: Int): ItemKey = copy(page = page + value)

    fun minus(value: Int): ItemKey = copy(page = page - value)

    companion object {
        val Default = ItemKey(page = 1, query = null)
    }
}

class ItemsPagingSource(private val qiitaRepository: QiitaRepository) : PagingSource<ItemKey, Item>() {
    override suspend fun load(params: LoadParams<ItemKey>): LoadResult<ItemKey, Item> {
        val (page, query) = params.key ?: ItemKey.Default
        val perPage = params.loadSize

        return when (val result = qiitaRepository.findItems(page = page, perPage = perPage, query = query)) {
            is QiitaApiResult.Success -> {
                val rateRemaining = result.rate?.rateRemaining ?: 0

                val prevKey = if (rateRemaining == 0) null else result.pagination?.prevPage?.let { ItemKey(it, query) }
                val nextKey = if (rateRemaining == 0) null else result.pagination?.nextPage?.let { ItemKey(it, query) }
                LoadResult.Page(data = result.response, prevKey = prevKey, nextKey = nextKey)
            }
            is QiitaApiResult.Failure.HttpFailure -> {
                Timber.e("HttpFailure, status code = ${result.statusCode}")
                LoadResult.Error(RuntimeException("HTTP ${result.statusCode} ${result.error.message}"))
            }
            is QiitaApiResult.Failure.NetworkFailure -> {
                Timber.e(result.exception, "NetworkFailure")
                LoadResult.Error(result.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<ItemKey, Item>): ItemKey? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}
