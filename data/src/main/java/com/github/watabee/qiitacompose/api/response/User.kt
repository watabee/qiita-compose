package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// https://qiita.com/api/v2/docs#%E3%83%A6%E3%83%BC%E3%82%B6
@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String?,
    val description: String?,
    val organization: String?,
    @Json(name = "followees_count") val followeesCount: Int,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "items_count") val itemsCount: Int,
    @Json(name = "profile_image_url") val profileImageUrl: String
)
