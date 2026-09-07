package com.mentorhomeloans.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.ui.theme.MentorBlue

@Composable
fun AboutAppScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "About App",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Mentor Home Loans", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
            Spacer(Modifier.height(8.dp))
            Text("Version 1.0.0", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Terms of Service",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.CmsContent.createRoute("termsconditions"))
                            }
                            .padding(vertical = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Privacy Policy",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.CmsContent.createRoute("privacypolicy"))
                            }
                            .padding(vertical = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "About Us",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.CmsContent.createRoute("aboutus"))
                            }
                            .padding(vertical = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Open Source Licenses", 
                        fontSize = 16.sp, 
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
