package com.github.watabee.qiitacompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// TODO: Set dark color palette
private val DarkColorPalette = darkColors()

private val LightColorPalette = lightColors(
    primary = green500,
    primaryVariant = green800,
    secondary = blueGrey600,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = grey800,
    onSurface = grey800
)

@Composable
fun QiitaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors =
        if (darkTheme) {
            DarkColorPalette
        } else {
            LightColorPalette
        }

    MaterialTheme(colors = colors, typography = typography, shapes = shapes, content = content)
}
