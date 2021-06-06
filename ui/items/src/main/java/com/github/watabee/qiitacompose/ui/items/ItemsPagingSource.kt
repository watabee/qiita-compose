package com.github.watabee.qiitacompose.ui.items

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.repository.QiitaRepository
import timber.log.Timber

internal class ItemsPagingSource(private val qiitaRepository: QiitaRepository) : PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val page = params.key ?: 1
        val perPage = params.loadSize

        return when (val result = qiitaRepository.findItems(page = page, perPage = perPage, query = null)) {
            is QiitaApiResult.Success -> {
                val rateRemaining = result.rate?.rateRemaining ?: 0

                val prevKey = if (rateRemaining == 0) null else result.pagination?.prevPage
                val nextKey = if (rateRemaining == 0) null else result.pagination?.nextPage
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

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}
