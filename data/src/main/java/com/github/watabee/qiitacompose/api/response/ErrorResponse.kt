package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(val message: String, val type: String)
