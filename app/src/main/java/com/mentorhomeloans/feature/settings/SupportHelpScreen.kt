package com.mentorhomeloans.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.ui.theme.MentorBlue

@Composable
fun SupportHelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Help & Customer Support",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero Callout for Raise Request / Grievance Portal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.Support.route) },
                colors = CardDefaults.cardColors(containerColor = MentorBlue),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color(0xFFF9A01B), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Grievance & Support Portal", color = Color(0xFFF9A01B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Raise & Track Support Tickets", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Statement issues, payment queries, loan queries & SLA resolution tracking", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Navigate", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct Contact Section
            Text("Direct Contact Channels", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactChannelItem(
                        icon = Icons.Default.Phone,
                        iconColor = MentorBlue,
                        title = "Toll-Free Helpline",
                        subtitle = "1800-123-4567 (Mon-Sat, 9:30 AM - 6:30 PM)"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(16.dp))

                    ContactChannelItem(
                        icon = Icons.Default.Email,
                        iconColor = Color(0xFF43A047),
                        title = "Email Support Desk",
                        subtitle = "customercare@mentorhomeloans.com"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(16.dp))

                    ContactChannelItem(
                        icon = Icons.Default.LocationOn,
                        iconColor = Color(0xFFE53935),
                        title = "Registered Corporate Office",
                        subtitle = "Mentor Home Loans India Ltd, Jaipur, Rajasthan"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "More Contact Details",
                        fontSize = 14.sp,
                        color = MentorBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.CmsContent.createRoute("contactus"))
                            }
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Common Support Categories Section
            Text("Support Categories", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SupportCategoryCard(
                    title = "Statement Issue",
                    desc = "IT proof & SOA requests",
                    icon = Icons.Default.Description,
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Support.route) }

                SupportCategoryCard(
                    title = "Payment Issue",
                    desc = "EMI debits & receipts",
                    icon = Icons.Default.Payment,
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Support.route) }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SupportCategoryCard(
                    title = "Loan Query",
                    desc = "Prepayment & interest ROI",
                    icon = Icons.Default.Help,
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Support.route) }

                SupportCategoryCard(
                    title = "Contact Update",
                    desc = "Mobile & address changes",
                    icon = Icons.Default.ContactMail,
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Support.route) }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ContactChannelItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SupportCategoryCard(
    title: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MentorBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
            Text(text = desc, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
