package com.github.watabee.qiitacompose.ui.user

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun UserScreen(userId: String) {
    Text(text = "user/$userId")
}
