package com.github.watabee.qiitacompose

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.util.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberAppState(
    qiitaClientId: String,
    userRepository: UserRepository,
    snackbarManager: SnackbarManager,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState = remember(userRepository, scaffoldState, navController, snackbarManager, resources, coroutineScope) {
    AppState(qiitaClientId, userRepository, scaffoldState, navController, snackbarManager, resources, coroutineScope)
}

@Stable
class AppState(
    val qiitaClientId: String,
    userRepository: UserRepository,
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    resources: Resources,
    coroutineScope: CoroutineScope,
) {
    val appRouter: AppRouting = AppRouter(navController, userRepository)

    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { messages ->
                val message = messages.firstOrNull() ?: return@collect
                val text = resources.getString(message.messageId)
                val action = message.action
                if (action != null) {
                    val actionText = resources.getString(action.messageId)
                    val result: SnackbarResult = scaffoldState.snackbarHostState.showSnackbar(text, actionText, duration = message.duration)
                    action.onDismiss?.invoke(result)
                } else {
                    scaffoldState.snackbarHostState.showSnackbar(text, duration = message.duration)
                }
                snackbarManager.setMessageShown(message.id)
            }
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
