package com.github.watabee.qiitacompose.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.github.watabee.qiitacompose.ui.common.R

// TODO: Set dark color palette
private val DarkColorPalette = darkColors()

@SuppressLint("ConflictingOnColor")
@Composable
private fun lightColorPalette(): Colors {
    return lightColors(
        primary = colorResource(id = R.color.green_60),
        primaryVariant = colorResource(id = R.color.green_80),
        secondary = colorResource(id = R.color.green_60),
        background = colorResource(id = R.color.white),
        surface = colorResource(id = R.color.white),
        onPrimary = colorResource(id = R.color.white),
        onBackground = colorResource(id = R.color.black),
        onSurface = colorResource(id = R.color.black),
    )
}

val Colors.tagBackground: Color
    get() = Color(0xFFEEEEEE)

@Composable
fun QiitaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else lightColorPalette()

    MaterialTheme(colors = colors, typography = typography, shapes = shapes, content = content)
}
