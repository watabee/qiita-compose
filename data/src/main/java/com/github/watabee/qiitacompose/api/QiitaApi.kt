package com.github.watabee.qiitacompose.api

import androidx.annotation.IntRange
import com.github.watabee.qiitacompose.api.response.ErrorResponse
import com.github.watabee.qiitacompose.api.response.Item
import com.slack.eithernet.ApiResult
import com.slack.eithernet.DecodeErrorBody
import retrofit2.http.GET
import retrofit2.http.Query

interface QiitaApi {

    @DecodeErrorBody
    @GET("/api/v2/items")
    suspend fun findItems(
        @IntRange(from = 1, to = 100)
        @Query("page")
        page: Int,
        @IntRange(from = 1, to = 100)
        @Query("per_page")
        perPage: Int,
        @Query("query")
        query: String?
    ): ApiResult<List<Item>, ErrorResponse>
}
