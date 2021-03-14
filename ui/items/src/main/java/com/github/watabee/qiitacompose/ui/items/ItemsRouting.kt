package com.github.watabee.qiitacompose.ui.items

import com.github.watabee.qiitacompose.api.response.User

interface ItemsRouting {
    val openUserScreen: (user: User) -> Unit
}
