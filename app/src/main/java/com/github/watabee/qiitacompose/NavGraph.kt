package com.github.watabee.qiitacompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.common.LocalNavHostController
import com.github.watabee.qiitacompose.ui.home.HomeScreen
import com.github.watabee.qiitacompose.ui.itemdetail.ItemDetailScreen
import com.github.watabee.qiitacompose.ui.login.LoginScreen
import com.github.watabee.qiitacompose.ui.mypage.MyPageScreen
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.search.SearchScreen
import com.github.watabee.qiitacompose.ui.user.UserScreen
import com.github.watabee.qiitacompose.util.Env
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object MainDestinations {
    const val HOME = "home"
    const val USER = "user/{userId}"
    const val MYPAGE = "mypage"
    const val SEARCH = "search"
    const val ITEM_DETAIL = "item/{url}"
    const val LOGIN = "login"
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.HOME, env: Env, userRepository: UserRepository) {
    val navController = rememberNavController()
    val appRouter = remember(navController, userRepository) {
        AppRouter(navController, userRepository)
    }

    CompositionLocalProvider(
        LocalNavHostController provides navController
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(MainDestinations.HOME) {
                HomeScreen(appRouting = appRouter)
            }
            composable(MainDestinations.USER) { backStackEntry ->
                UserScreen(
                    userId = backStackEntry.arguments?.getString("userId")!!,
                    appRouting = appRouter,
                    navigateUp = { navController.navigateUp() }
                )
            }
            composable(MainDestinations.MYPAGE) {
                MyPageScreen { navController.popBackStack() }
            }
            composable(MainDestinations.SEARCH) {
                SearchScreen(
                    openUserScreen = appRouter.openUserScreen,
                    openItemDetailScreen = appRouter.openItemDetailScreen,
                    closeSearchScreen = { navController.popBackStack() }
                )
            }
            composable(MainDestinations.ITEM_DETAIL) { backStackEntry ->
                ItemDetailScreen(url = URLDecoder.decode(backStackEntry.arguments?.getString("url")!!, StandardCharsets.UTF_8.toString()))
            }
            composable(MainDestinations.LOGIN) {
                LoginScreen(qiitaClientId = env.qiitaClientId) { navController.navigateUp() }
            }
        }
    }
}

class AppRouter(
    navController: NavController,
    private val userRepository: UserRepository
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
