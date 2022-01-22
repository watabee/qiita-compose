package com.github.watabee.qiitacompose.ui.user

import androidx.compose.runtime.Stable
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.state.ToastMessage

@Stable
data class UserUiModel(
    val isLoading: Boolean = false,
    val getUserInfoError: Boolean = false,
    val user: User? = null,
    val followButtonState: FollowButtonState = FollowButtonState.LOGIN_REQUIRED,
    val followingTags: List<Tag> = emptyList(),
    val toastMessages: List<ToastMessage> = emptyList()
) {
    enum class FollowButtonState {
        PROCESSING, FOLLOWING, UNFOLLOWING, LOGIN_REQUIRED,
    }
}
