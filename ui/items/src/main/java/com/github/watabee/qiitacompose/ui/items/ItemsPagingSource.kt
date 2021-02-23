package com.github.watabee.qiitacompose.ui.items

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.SuccessResponseWithPagination
import com.github.watabee.qiitacompose.repository.QiitaRepository
import timber.log.Timber

internal class ItemsPagingSource(private val qiitaRepository: QiitaRepository) : PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val page = params.key ?: 1
        val perPage = params.loadSize

        return when (val result = qiitaRepository.findItems(page = page, perPage = perPage, query = null)) {
            is QiitaApiResult.Success -> {
                val response: SuccessResponseWithPagination<List<Item>> = result.response
                val nextKey = if (response.rate?.rateRemaining == 0) null else response.nextPage
                LoadResult.Page(data = response.response, prevKey = null, nextKey = nextKey)
            }
            is QiitaApiResult.Failure.HttpFailure -> {
                Timber.e("HttpFailure, code = ${result.code}")
                LoadResult.Error(RuntimeException("HTTP ${result.code} ${result.error?.error?.message.orEmpty()}"))
            }
            is QiitaApiResult.Failure.NetworkFailure -> {
                Timber.e(result.error, "NetworkFailure")
                LoadResult.Error(result.error)
            }
            is QiitaApiResult.Failure.UnknownFailure -> {
                Timber.e(result.error, "UnknownFailure")
                LoadResult.Error(result.error)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? = null
}
