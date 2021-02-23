package com.github.watabee.qiitacompose.api.interceptor

import com.github.watabee.qiitacompose.datastore.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class AccessTokenInterceptor @Inject constructor(userDataStore: UserDataStore) : Interceptor {
    private val scope = CoroutineScope(Job())
    private var accessToken: String? = null

    init {
        userDataStore.accessTokenFlow
            .onEach { accessToken = it }
            .launchIn(scope)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = accessToken

        val request = if (!accessToken.isNullOrBlank()) {
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
