package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Item(
    val id: String,
    val title: String,
    @Json(name = "created_at")
    val createdAt: Date,
    @Json(name = "updated_at")
    val updatedAt: Date,
    val url: String
)
