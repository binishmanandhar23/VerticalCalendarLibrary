package io.github.binishmanandhar23.verticalcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Purple200,
    primaryVariant = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Purple700,
    secondary = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Teal200
)

private val LightColorPalette = lightColors(
    primary = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Purple500,
    primaryVariant = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Purple700,
    secondary = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun VerticalCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Typography,
        shapes = io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.Shapes,
        content = content
    )
}