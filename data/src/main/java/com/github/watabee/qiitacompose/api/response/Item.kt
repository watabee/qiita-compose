package com.github.watabee.qiitacompose.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Item(
    val id: String,
    val title: String,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "updated_at") val updatedAt: Date,
    val url: String,
    val user: User,
    @Json(name = "likes_count") val likesCount: Int,
    @Json(name = "reactions_count") val reactionsCount: Int,
    @Json(name = "page_views_count") val pageViewsCount: Int?,
    @Json(name = "comments_count") val commentsCount: Int,
    val tags: List<Tag>
) {
    @JsonClass(generateAdapter = true)
    data class Tag(val name: String)
}
