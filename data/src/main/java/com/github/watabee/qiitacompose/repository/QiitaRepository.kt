package com.github.watabee.qiitacompose.repository

import androidx.annotation.IntRange
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.await
import com.github.watabee.qiitacompose.api.response.ErrorResponse
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.di.Api
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly

interface QiitaRepository {
    suspend fun findItems(
        @IntRange(from = 1, to = 100)
        page: Int,
        @IntRange(from = 1, to = 100)
        perPage: Int,
        query: String?
    ): QiitaApiResult<List<Item>, ErrorResponse>
}

internal class QiitaRepositoryImpl
    @Inject
    constructor(@Api private val okHttpClient: OkHttpClient, private val moshi: Moshi) :
    QiitaRepository {

    override suspend fun findItems(
        @IntRange(from = 1, to = 100)
        page: Int,
        @IntRange(from = 1, to = 100)
        perPage: Int,
        query: String?
    ): QiitaApiResult<List<Item>, ErrorResponse> {
        val httpUrl =
            HttpUrl.Builder()
                .scheme("https")
                .host("qiita.com")
                .addPathSegments("api/v2/items")
                .addQueryParameter("page", page.toString())
                .addQueryParameter("per_page", perPage.toString())
                .apply {
                    if (!query.isNullOrBlank()) {
                        addQueryParameter("query", query)
                    }
                }
                .build()

        return httpGet(httpUrl)
    }

    private suspend inline fun <reified T : Any, reified E> httpGet(
        httpUrl: HttpUrl
    ): QiitaApiResult<T, E> {
        val request = Request.Builder().url(httpUrl).get().build()

        return try {
            parseResponse(okHttpClient.newCall(request).await())
        } catch (e: IOException) {
            QiitaApiResult.Failure.NetworkFailure(e)
        } catch (e: Throwable) {
            QiitaApiResult.Failure.UnknownFailure(e)
        }
    }

    private inline fun <reified T : Any, reified E> parseResponse(
        response: Response
    ): QiitaApiResult<T, E> {
        val source = response.body?.source()
        try {
            return if (response.isSuccessful) {
                QiitaApiResult.Success(moshi.adapter(T::class.java).fromJson(source)!!)
            } else {
                QiitaApiResult.Failure.HttpFailure(
                    response.code, moshi.adapter(E::class.java).fromJson(source)!!)
            }
        } finally {
            source?.closeQuietly()
        }
    }
}
