package com.mentorhomeloans.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark colour scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = MentorBlueMid,
    onPrimary          = NeutralWhite,
    primaryContainer   = MentorBlueDark,
    onPrimaryContainer = MentorBluePale,
    secondary          = MentorBlueLight,
    onSecondary        = MentorBlueDeep,
    tertiary           = MentorOrange,
    onTertiary         = NeutralBlack,
    background         = Color(0xFF0D1B2A),
    onBackground       = NeutralWhite,
    surface            = Color(0xFF122334),
    onSurface          = NeutralWhite,
    error              = MentorError,
)

// ── Light colour scheme ───────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = MentorBlue,          // #006EB1
    onPrimary          = NeutralWhite,
    primaryContainer   = MentorBluePale,
    onPrimaryContainer = MentorBlueDark,
    secondary          = MentorBlueLight,     // light shade for nav bar
    onSecondary        = MentorBlueDeep,
    tertiary           = MentorOrange,
    onTertiary         = NeutralBlack,
    background         = NeutralSurface,
    onBackground       = Color(0xFF1C1B1F),
    surface            = NeutralWhite,
    onSurface          = Color(0xFF1C1B1F),
    surfaceVariant     = MentorBlueXPale,
    error              = MentorError,
)

@Composable
fun MentorHomeLoansTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled so our brand colours always show
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MentorBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}