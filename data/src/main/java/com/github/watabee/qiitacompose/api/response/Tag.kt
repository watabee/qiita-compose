package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// https://qiita.com/api/v2/docs#%E3%82%BF%E3%82%B0
@JsonClass(generateAdapter = true)
data class Tag(
    val id: String,
    @Json(name = "items_count") val itemsCount: Int,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "icon_url") val iconUrl: String
)
