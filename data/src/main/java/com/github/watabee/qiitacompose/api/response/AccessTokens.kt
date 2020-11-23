package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokens(
    @Json(name = "client_id") val clientId: String,
    val scopes: List<String>,
    val token: String
)
