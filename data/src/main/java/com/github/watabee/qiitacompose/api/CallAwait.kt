package com.github.watabee.qiitacompose.api

import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) {
                        return
                    }
                    continuation.resume(response)
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled) {
                        return
                    }
                    continuation.resumeWithException(e)
                }
            })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (e: Throwable) {
                // ignore error
            }
        }
    }
}
