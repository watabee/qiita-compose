package com.github.watabee.qiitacompose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.watabee.qiitacompose.ui.home.HomeScreen
import com.github.watabee.qiitacompose.ui.itemdetail.ItemDetailScreen
import com.github.watabee.qiitacompose.ui.login.LoginScreen
import com.github.watabee.qiitacompose.ui.mypage.MyPageScreen
import com.github.watabee.qiitacompose.ui.search.SearchScreen
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.github.watabee.qiitacompose.ui.user.UserScreen
import com.google.accompanist.insets.ProvideWindowInsets
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun QiitaApp(appState: AppState) {
    ProvideWindowInsets {
        QiitaTheme {
            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                scaffoldState = appState.scaffoldState,
            ) { paddingValues ->
                NavHost(
                    navController = appState.navController,
                    startDestination = MainDestinations.HOME,
                    modifier = Modifier.padding(paddingValues),
                ) {
                    qiitaAppNavGraph(appState)
                }
            }
        }
    }
}

private fun NavGraphBuilder.qiitaAppNavGraph(appState: AppState) {
    composable(MainDestinations.HOME) {
        HomeScreen(appRouting = appState.appRouter)
    }
    composable(MainDestinations.USER) { backStackEntry ->
        UserScreen(
            userId = backStackEntry.arguments?.getString("userId")!!,
            appRouting = appState.appRouter,
            navigateUp = appState::navigateUp,
        )
    }
    composable(MainDestinations.MYPAGE) {
        MyPageScreen { appState.navigateUp() }
    }
    composable(MainDestinations.SEARCH) {
        SearchScreen(
            openUserScreen = appState.appRouter.openUserScreen,
            openItemDetailScreen = appState.appRouter.openItemDetailScreen,
            closeSearchScreen = appState::navigateUp,
        )
    }
    composable(MainDestinations.ITEM_DETAIL) { backStackEntry ->
        ItemDetailScreen(url = URLDecoder.decode(backStackEntry.arguments?.getString("url")!!, StandardCharsets.UTF_8.toString()))
    }
    composable(MainDestinations.LOGIN) {
        LoginScreen(qiitaClientId = appState.qiitaClientId, navigateUp = appState::navigateUp)
    }
}
