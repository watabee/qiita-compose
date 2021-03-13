package com.github.watabee.qiitacompose.ui.home

interface HomeRouting {
    val openLoginScreen: () -> Unit

    val openUserScreen: (userId: String) -> Unit
}
