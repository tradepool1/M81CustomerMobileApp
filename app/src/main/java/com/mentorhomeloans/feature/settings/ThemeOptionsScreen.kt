package com.mentorhomeloans.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.AppTopBar

@Composable
fun ThemeOptionsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Theme Options",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FE))
                .padding(16.dp)
        ) {
            Text("Select Theme", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            // Placeholder for theme selection (System, Light, Dark)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("System Default", fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Light Mode", fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Dark Mode", fontSize = 16.sp)
                }
            }
        }
    }
}
