package com.github.watabee.qiitacompose

import androidx.navigation.NavController
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal object MainDestinations {
    const val HOME = "home"
    const val USER = "user/{userId}"
    const val MYPAGE = "mypage"
    const val SEARCH = "search"
    const val ITEM_DETAIL = "item/{url}"
    const val LOGIN = "login"
}

internal class AppRouter(
    navController: NavController,
    private val userRepository: UserRepository,
) : AppRouting {
    override val openUserScreen: suspend (user: User) -> Unit = { user ->
        userRepository.insertOrUpdate(user)
        navController.navigate("user/${user.id}")
    }

    override val openMyPageScreen: () -> Unit = {
        navController.navigate(MainDestinations.MYPAGE)
    }

    override val openSearchScreen: () -> Unit = {
        navController.navigate(MainDestinations.SEARCH)
    }

    override val openItemDetailScreen: (url: String) -> Unit = {
        navController.navigate("item/${URLEncoder.encode(it, StandardCharsets.UTF_8.toString())}")
    }

    override val openLoginScreen: () -> Unit = {
        navController.navigate(MainDestinations.LOGIN)
    }
}
