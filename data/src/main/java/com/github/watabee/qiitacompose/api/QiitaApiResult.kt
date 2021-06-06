package com.github.watabee.qiitacompose.api

import com.github.watabee.qiitacompose.api.response.Error
import com.github.watabee.qiitacompose.api.response.Pagination
import com.github.watabee.qiitacompose.api.response.Rate
import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import com.squareup.moshi.Moshi

sealed class QiitaApiResult<out T> {
    data class Success<T : Any>(val response: T, val rate: Rate?, val pagination: Pagination? = null) : QiitaApiResult<T>() {

        internal class Mapper<T : Any> : ApiSuccessModelMapper<T, Success<T>> {
            override fun map(apiErrorResponse: ApiResponse.Success<T>): Success<T> {
                return with(apiErrorResponse) {
                    Success(data!!, Rate.parseHeaders(headers), pagination = Pagination(headers))
                }
            }
        }

        object EmptyMapper : ApiSuccessModelMapper<Unit, Success<Unit>> {
            override fun map(apiErrorResponse: ApiResponse.Success<Unit>): Success<Unit> {
                return with(apiErrorResponse) {
                    Success(Unit, Rate.parseHeaders(headers), pagination = Pagination(headers))
                }
            }
        }
    }

    sealed class Failure : QiitaApiResult<Nothing>() {
        data class HttpFailure(val statusCode: Int, val error: Error, val rate: Rate?) : Failure() {

            internal class Mapper(val moshi: Moshi) : ApiErrorModelMapper<HttpFailure> {
                override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): HttpFailure {
                    return with(apiErrorResponse) {
                        HttpFailure(
                            statusCode.code,
                            moshi.adapter(Error::class.java).fromJson(errorBody!!.source())!!,
                            Rate.parseHeaders(headers)
                        )
                    }
                }
            }
        }

        class NetworkFailure(val exception: Throwable) : Failure()
    }
}
