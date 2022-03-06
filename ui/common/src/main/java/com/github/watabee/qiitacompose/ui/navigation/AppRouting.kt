package com.github.watabee.qiitacompose.ui.navigation

import androidx.compose.runtime.Stable
import com.github.watabee.qiitacompose.api.response.User

@Stable
interface AppRouting {
    val openLoginScreen: () -> Unit

    val openUserScreen: suspend (user: User) -> Unit

    val openMyPageScreen: () -> Unit

    val openSearchScreen: () -> Unit

    val openItemDetailScreen: (url: String) -> Unit
}
