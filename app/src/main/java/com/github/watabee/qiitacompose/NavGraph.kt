package com.github.watabee.qiitacompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.watabee.qiitacompose.ui.common.LocalNavHostController
import com.github.watabee.qiitacompose.ui.home.HomeRouting
import com.github.watabee.qiitacompose.ui.home.HomeScreen
import com.github.watabee.qiitacompose.ui.items.ItemsRouting
import com.github.watabee.qiitacompose.ui.user.UserScreen

object MainDestinations {
    const val HOME = "home"
    const val USER = "user/{userId}"
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.HOME, openLoginScreen: () -> Unit) {
    val navController = rememberNavController()
    val appRouter = remember(navController) { AppRouter(navController, openLoginScreen) }

    CompositionLocalProvider(
        LocalNavHostController provides navController
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(MainDestinations.HOME) {
                HomeScreen(homeRouting = appRouter, itemsRouting = appRouter)
            }
            composable(MainDestinations.USER) { backStackEntry ->
                UserScreen(backStackEntry.arguments?.getString("userId").orEmpty())
            }
        }
    }
}

class AppRouter(navController: NavController, override val openLoginScreen: () -> Unit) : HomeRouting, ItemsRouting {
    override val openUserScreen: (userId: String) -> Unit = { userId ->
        navController.navigate("user/$userId")
    }
}
