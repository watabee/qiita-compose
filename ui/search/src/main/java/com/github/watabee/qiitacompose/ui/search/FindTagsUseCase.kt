package com.github.watabee.qiitacompose.ui.search

import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.request.SortTag
import com.github.watabee.qiitacompose.datastore.AppDataStore
import com.github.watabee.qiitacompose.db.dao.TagDao
import com.github.watabee.qiitacompose.repository.QiitaRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class FindTagsUseCase @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val tagDao: TagDao,
    private val appDataStore: AppDataStore
) {
    suspend fun execute() {
        val cacheMillis = TimeUnit.DAYS.toMillis(1L)
        if (System.currentTimeMillis() - appDataStore.lastTagsFetchedAt() < cacheMillis) {
            return
        }
        tagDao.deleteAll()

        when (val result = qiitaRepository.findTags(page = 1, perPage = 20, sortTag = SortTag.COUNT)) {
            is QiitaApiResult.Success -> {
                tagDao.insert(result.response)
                appDataStore.updateLastTagsFetchedAt(System.currentTimeMillis())
            }
            is QiitaApiResult.Failure.HttpFailure -> {
                Timber.e("HttpFailure: ${result.error}")
            }
            is QiitaApiResult.Failure.NetworkFailure -> {
                Timber.e("NetworkFailure: ${result.exception}")
            }
        }
    }
}
