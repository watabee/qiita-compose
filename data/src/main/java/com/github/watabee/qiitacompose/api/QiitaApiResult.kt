package com.github.watabee.qiitacompose.api

import java.io.IOException

sealed class QiitaApiResult<out T, out E> {

    data class Success<T : Any>(val response: T) : QiitaApiResult<T, Nothing>()

    sealed class Failure<out E> : QiitaApiResult<Nothing, E>() {
        data class NetworkFailure internal constructor(val error: IOException) : Failure<Nothing>()

        data class UnknownFailure internal constructor(val error: Throwable) : Failure<Nothing>()

        data class HttpFailure<out E> internal constructor(val code: Int, val error: E?) :
            Failure<E>()
    }
}
