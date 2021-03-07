package com.github.watabee.qiitacompose.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState

// https://github.com/google/dagger/issues/2166#issuecomment-769162910
@Composable
inline fun <reified VM : ViewModel> navViewModel(key: String? = null): VM {
    val navController = LocalNavHostController.current
    val backStackEntryState: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val backStackEntry = backStackEntryState ?: return viewModel(key)
    return viewModel(key, HiltViewModelFactory(LocalContext.current, backStackEntry))
}
