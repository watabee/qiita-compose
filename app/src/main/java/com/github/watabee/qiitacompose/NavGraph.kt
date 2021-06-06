package com.github.watabee.qiitacompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.common.LocalNavHostController
import com.github.watabee.qiitacompose.ui.home.HomeScreen
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.user.UserScreen

object MainDestinations {
    const val HOME = "home"
    const val USER = "user"
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.HOME, openLoginScreen: () -> Unit) {
    val navController = rememberNavController()
    val appRouter = remember(navController, openLoginScreen) { AppRouter(navController, openLoginScreen) }

    CompositionLocalProvider(
        LocalNavHostController provides navController
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(MainDestinations.HOME) {
                HomeScreen(appRouting = appRouter)
            }
            composable(MainDestinations.USER) {
                UserScreen(
                    user = navController.previousBackStackEntry?.arguments?.getParcelable("user")!!,
                    appRouting = appRouter
                )
            }
        }
    }
}

class AppRouter(navController: NavController, override val openLoginScreen: () -> Unit) : AppRouting {
    override val openUserScreen: (user: User) -> Unit = { user ->
        navController.currentBackStackEntry?.arguments = bundleOf("user" to user)
        navController.navigate(MainDestinations.USER)
    }
}
