package com.github.watabee.qiitacompose.repository

import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.QiitaApiService
import com.github.watabee.qiitacompose.api.request.SortTag
import com.github.watabee.qiitacompose.api.response.AccessTokens
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.Pagination
import com.github.watabee.qiitacompose.api.response.Rate
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.util.Env
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.request
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

interface QiitaRepository {

    suspend fun findItems(page: Int, perPage: Int, query: String?): QiitaApiResult<List<Item>>

    suspend fun getAccessTokens(code: String): QiitaApiResult<AccessTokens>

    suspend fun getAuthenticatedUser(accessToken: String): QiitaApiResult<AuthenticatedUser>

    suspend fun isFollowingUser(userId: String): QiitaApiResult<Boolean>

    suspend fun getUserFollowingTags(userId: String): QiitaApiResult<List<Tag>>

    suspend fun followUser(userId: String): QiitaApiResult<Unit>

    suspend fun unfollowUser(userId: String): QiitaApiResult<Unit>

    suspend fun findTags(page: Int, perPage: Int, sortTag: SortTag): QiitaApiResult<List<Tag>>
}

internal class QiitaRepositoryImpl @Inject constructor(
    private val moshi: Moshi,
    private val qiitaApiService: QiitaApiService,
    private val env: Env,
) : QiitaRepository {

    private suspend fun <T : Any> ApiResponse<T>.toApiResult(
        successMapper: ApiSuccessModelMapper<T, QiitaApiResult.Success<T>> = QiitaApiResult.Success.Mapper(),
    ): QiitaApiResult<T> = flow {
        suspendOnSuccess(successMapper) { emit(this) }
            .suspendOnError(QiitaApiResult.Failure.HttpFailure.Mapper(moshi)) { emit(this) }
            .suspendOnException { emit(QiitaApiResult.Failure.NetworkFailure(exception)) }
    }.first()

    override suspend fun findItems(page: Int, perPage: Int, query: String?): QiitaApiResult<List<Item>> {
        return qiitaApiService.findItems(page, perPage, query).toApiResult()
    }

    override suspend fun getAccessTokens(code: String): QiitaApiResult<AccessTokens> {
        val body = mapOf("client_id" to env.qiitaClientId, "client_secret" to env.qiitaClientSecret, "code" to code)
        return qiitaApiService.requestAccessTokens(body).toApiResult()
    }

    override suspend fun getAuthenticatedUser(accessToken: String): QiitaApiResult<AuthenticatedUser> {
        return qiitaApiService.fetchAuthenticatedUser("Bearer $accessToken").toApiResult()
    }

    override suspend fun isFollowingUser(userId: String): QiitaApiResult<Boolean> = suspendCancellableCoroutine { continuation ->
        val call = qiitaApiService.isFollowingUser(userId).request { response: ApiResponse<Unit> ->
            // If the user is followed -> status code: 204
            // If the user is not followed -> status code: 404
            val result = when (response) {
                is ApiResponse.Success -> {
                    QiitaApiResult.Success(
                        response = true,
                        rate = Rate.parseHeaders(response.headers),
                        pagination = Pagination(response.headers),
                    )
                }
                is ApiResponse.Failure.Error -> {
                    if (response.statusCode == StatusCode.NotFound) {
                        QiitaApiResult.Success(
                            response = false,
                            rate = Rate.parseHeaders(response.headers),
                            pagination = Pagination(response.headers),
                        )
                    } else {
                        QiitaApiResult.Failure.HttpFailure.Mapper(moshi).map(response)
                    }
                }
                is ApiResponse.Failure.Exception -> {
                    QiitaApiResult.Failure.NetworkFailure(response.exception)
                }
            }
            if (!continuation.isCancelled) {
                continuation.resume(result)
            }
        }

        continuation.invokeOnCancellation {
            kotlin.runCatching { call.cancel() }
        }
    }

    override suspend fun getUserFollowingTags(userId: String): QiitaApiResult<List<Tag>> {
        return qiitaApiService.fetchFollowingTags(userId).toApiResult()
    }

    override suspend fun followUser(userId: String): QiitaApiResult<Unit> {
        return qiitaApiService.followUser(userId).toApiResult(successMapper = QiitaApiResult.Success.EmptyMapper)
    }

    override suspend fun unfollowUser(userId: String): QiitaApiResult<Unit> {
        return qiitaApiService.unfollowUser(userId).toApiResult(successMapper = QiitaApiResult.Success.EmptyMapper)
    }

    override suspend fun findTags(page: Int, perPage: Int, sortTag: SortTag): QiitaApiResult<List<Tag>> {
        return qiitaApiService.findTags(page = page, perPage = perPage, sort = sortTag).toApiResult()
    }
}
