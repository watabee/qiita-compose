package com.github.watabee.qiitacompose.api.response

import okhttp3.Headers
import java.util.Date

data class Rate internal constructor(val rateLimit: Int, val rateRemaining: Int, val rateReset: Date) {

    companion object {
        fun parseHeaders(headers: Headers): Rate? {
            val rateLimit = headers["rate-limit"]?.toIntOrNull() ?: return null
            val rateRemaining = headers["rate-remaining"]?.toIntOrNull() ?: return null
            val rateReset = headers["rate-reset"]?.toLongOrNull() ?: return null

            return Rate(rateLimit, rateRemaining, Date(rateReset * 1000L))
        }
    }
}
