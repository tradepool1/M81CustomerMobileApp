package com.mentorhomeloans.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.R
import com.mentorhomeloans.core.common.Constants
import com.mentorhomeloans.core.navigation.Screen
import kotlinx.coroutines.delay

/**
 * Animated Splash Screen showing brand logo and forwarding to Onboarding or Dashboard.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    isSessionActive: Boolean,
    isOnboardingCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        delay(Constants.SPLASH_DURATION_MS)
        val destination = when {
            isSessionActive -> Screen.Dashboard.route
            isOnboardingCompleted -> Screen.Login.route
            else -> Screen.Onboarding.route
        }
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Splash Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }
}
