package com.github.watabee.qiitacompose.api.response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticatedUser(
    @Json(name = "description") val description: String?,
    @Json(name = "facebook_id") val facebookId: String?,
    @Json(name = "followees_count") val followeesCount: Int,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "github_login_name") val githubLoginName: String?,
    @Json(name = "id") val id: String,
    @Json(name = "image_monthly_upload_limit") val imageMonthlyUploadLimit: Int,
    @Json(name = "image_monthly_upload_remaining") val imageMonthlyUploadRemaining: Int,
    @Json(name = "items_count") val itemsCount: Int,
    @Json(name = "linkedin_id") val linkedinId: String?,
    @Json(name = "location") val location: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "organization") val organization: String?,
    @Json(name = "permanent_id") val permanentId: Int,
    @Json(name = "profile_image_url") val profileImageUrl: String,
    @Json(name = "team_only") val teamOnly: Boolean,
    @Json(name = "twitter_screen_name") val twitterScreenName: String?,
    @Json(name = "website_url") val websiteUrl: String?
)
