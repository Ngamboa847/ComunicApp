package com.example.comunicappescolar.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary          = Blue800,
    onPrimary        = White,
    primaryContainer = Blue50,
    secondary        = Blue700,
    background       = Blue50,
    surface          = White,
    error            = ColorError
)

@Composable
fun ComunicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = Typography,
        content     = content
    )
}
