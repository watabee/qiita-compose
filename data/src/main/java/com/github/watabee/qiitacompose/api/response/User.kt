package com.github.watabee.qiitacompose.api.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// https://qiita.com/api/v2/docs#%E3%83%A6%E3%83%BC%E3%82%B6
@JsonClass(generateAdapter = true)
@Parcelize
data class User(
    val id: String,
    val name: String?,
    val description: String?,
    val organization: String?,
    val location: String?,
    @Json(name = "followees_count") val followeesCount: Int,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "items_count") val itemsCount: Int,
    @Json(name = "profile_image_url") val profileImageUrl: String,
    @Json(name = "facebook_id") val facebookId: String?,
    @Json(name = "github_login_name") val githubLoginName: String?,
    @Json(name = "linkedin_id") val linkedinId: String?,
    @Json(name = "twitter_screen_name") val twitterScreenName: String?,
    @Json(name = "website_url") val websiteUrl: String?,
) : Parcelable
