package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(val message: String, val type: String)
