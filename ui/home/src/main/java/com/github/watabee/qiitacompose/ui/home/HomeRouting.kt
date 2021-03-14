package com.github.watabee.qiitacompose.ui.home

import com.github.watabee.qiitacompose.api.response.User

interface HomeRouting {
    val openLoginScreen: () -> Unit

    val openUserScreen: (user: User) -> Unit
}
