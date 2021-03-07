package com.github.watabee.qiitacompose.ui.common

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavHostController = compositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavHostController not present")
}
