package com.github.watabee.qiitacompose.api

import androidx.annotation.IntRange
import com.github.watabee.qiitacompose.api.request.SortTag
import com.github.watabee.qiitacompose.api.response.AccessTokens
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.Tag
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface QiitaApiService {

    @GET("/api/v2/items")
    suspend fun findItems(
        @IntRange(from = 1, to = 100) @Query("page") page: Int,
        @IntRange(from = 1, to = 100) @Query("per_page") perPage: Int,
        @Query("query") query: String?
    ): ApiResponse<List<Item>>

    @Headers("Content-Type: application/json")
    @POST("/api/v2/access_tokens")
    suspend fun requestAccessTokens(@Body body: Map<String, String>): ApiResponse<AccessTokens>

    @GET("/api/v2/authenticated_user")
    suspend fun fetchAuthenticatedUser(@Header("Authorization") bearerToken: String): ApiResponse<AuthenticatedUser>

    @GET("/api/v2/users/{userId}/following")
    fun isFollowingUser(@Path("userId") userId: String): Call<Unit>

    @GET("/api/v2/users/{userId}/following_tags")
    suspend fun fetchFollowingTags(@Path("userId") userId: String): ApiResponse<List<Tag>>

    @PUT("/api/v2/users/{userId}/following")
    suspend fun followUser(@Path("userId") userId: String): ApiResponse<Unit>

    @DELETE("/api/v2/users/{userId}/following")
    suspend fun unfollowUser(@Path("userId") userId: String): ApiResponse<Unit>

    @GET("/api/v2/tags")
    suspend fun findTags(
        @IntRange(from = 1, to = 100) @Query("page") page: Int,
        @IntRange(from = 1, to = 100) @Query("per_page") perPage: Int,
        @Query("sort") sort: SortTag?
    ): ApiResponse<List<Tag>>
}
