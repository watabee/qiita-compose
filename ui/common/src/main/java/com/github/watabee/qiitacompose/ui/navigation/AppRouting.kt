package com.github.watabee.qiitacompose.ui.navigation

import com.github.watabee.qiitacompose.api.response.User

interface AppRouting {
    val openLoginScreen: () -> Unit

    val openUserScreen: suspend (user: User) -> Unit

    val openMyPageScreen: () -> Unit

    val openSearchScreen: () -> Unit
}
