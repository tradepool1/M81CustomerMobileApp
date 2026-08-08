package com.mentorhomeloans.feature.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.core.ui.components.BottomNavBar
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import androidx.compose.ui.res.stringResource
import com.mentorhomeloans.R
import com.mentorhomeloans.ui.theme.MentorBlue
import com.mentorhomeloans.ui.theme.MentorBlueXPale
import com.mentorhomeloans.ui.theme.MentorBluePale

/**
 * Dashboard screen laying out financial stats, quick shortcuts and details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Using a simple icon for the logo
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Logo",
                            tint = Color(0xFFF9A01B), // Orange tint
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.dashboard_title), fontWeight = FontWeight.Bold, color = MentorBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.Gray)
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FE) // Off-white/light-blue background matching the image
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route ->
                    if (route != Screen.Dashboard.route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MentorBlueXPale)
        ) {
            when (val state = uiState) {
                is DashboardUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardUIState.Success -> {
                    val loan = state.selectedLoan
                    val allLoans = state.allLoans
                    
                    var expanded by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Loan Selector Dropdown
                        if (allLoans.size > 1) {
                            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Text(text = "Loan A/C: ${loan.accountNumber}", color = MentorBlue, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Loan", tint = Color.Gray)
                                }
                                
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    allLoans.forEachIndexed { index, loanOption ->
                                        DropdownMenuItem(
                                            text = { Text("${loanOption.loanType.displayName} - ${loanOption.accountNumber}") },
                                            onClick = {
                                                viewModel.selectLoan(index)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Hero Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = MentorBlue) // Brand blue card
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                // Background watermark (approximation of the house vector)
                                Icon(
                                    imageVector = Icons.Outlined.Home,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.1f),
                                    modifier = Modifier
                                        .size(160.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 20.dp, y = 20.dp)
                                )

                                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = loan.loanType.displayName, color = Color.White, fontSize = 12.sp)
                                        }
                                        Text(
                                            text = loan.status.displayName,
                                            color = Color(0xFF00BFA5), // Green text
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFFE0F2F1), RoundedCornerShape(4.dp)) // Light green background
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    
                                    Text(text = loan.accountNumber, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.weight(1f))

                                    Column {
                                        Text(text = "Sanctioned:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                        Text(
                                            text = CurrencyUtils.formatINR(loan.sanctionAmount),
                                            color = Color.White,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Outstanding Principal: ${CurrencyUtils.formatINR(loan.outstandingAmount)}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                            Text(text = "Interest Rate: ${CurrencyUtils.formatInterestRate(loan.interestRate)}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { loan.repaymentProgress },
                                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                            color = Color(0xFFF9A01B), // Orange track
                                            trackColor = Color.White.copy(alpha = 0.3f),
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next EMI Details
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color(0xFFF9A01B))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = "Next EMI Details", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = CurrencyUtils.formatINR(loan.emiAmount), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                                
                                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                                
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = MentorBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "Due On", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = loan.nextEmiDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick Actions Grid
                        Text(text = "Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            QuickActionCard(
                                label = "Repayments",
                                icon = Icons.Default.CurrencyRupee, // In some compose versions CurrencyRupee isn't available, but let's assume it is or use Payment
                                iconColor = Color(0xFFFF7F00),
                                bgColor = Color.White
                            ) { navController.navigate(Screen.Repayment.route) }
                            
                            QuickActionCard(
                                label = "Statements",
                                icon = Icons.Default.Description,
                                iconColor = MentorBlue,
                                bgColor = Color.White
                            ) { navController.navigate(Screen.Statements.route) }
                            
                            QuickActionCard(
                                label = "Documents",
                                icon = Icons.Default.Folder,
                                iconColor = Color(0xFF43A047),
                                bgColor = Color.White
                            ) { navController.navigate(Screen.Documents.route) }
                            
                            QuickActionCard(
                                label = "Support",
                                icon = Icons.Default.HeadsetMic,
                                iconColor = Color(0xFF8E24AA),
                                bgColor = Color.White
                            ) { navController.navigate(Screen.Support.route) }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Loan Overview Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = stringResource(R.string.section_loan_overview), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val amountPaid = loan.sanctionAmount - loan.outstandingAmount
                                // Mock interest paid for now
                                val interestPaid = amountPaid * 0.28 // Just a mocked ratio for UI
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    OverviewStatItem(label = stringResource(R.string.label_amount_paid), value = CurrencyUtils.formatINR(amountPaid), icon = Icons.Default.PieChart, iconColor = Color(0xFFFF7F00))
                                    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                                    OverviewStatItem(label = stringResource(R.string.label_principle_paid), value = CurrencyUtils.formatINR(loan.principlReceived), icon = Icons.Default.CurrencyRupee, iconColor = MentorBlue)
                                    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                                    OverviewStatItem(label = stringResource(R.string.label_interest_paid), value = CurrencyUtils.formatINR(interestPaid), icon = Icons.Default.Percent, iconColor = Color(0xFF43A047))
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // View Loan Details button
                                OutlinedButton(
                                    onClick = { navController.navigate(Screen.LoanDetails.createRoute(loan.id)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(stringResource(R.string.btn_view_loan_details), color = MentorBlue, fontSize = 14.sp)
                                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Top-Up Ad Banner
                        Image(
                            painter = painterResource(id = com.mentorhomeloans.R.drawable.home_banner),
                            contentDescription = "Home Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { navController.navigate(Screen.SupportHelp.route) },
                            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
                        )

                        Spacer(modifier = Modifier.height(32.dp)) // Bottom spacing
                    }
                }
                is DashboardUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadLoanData() })
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    label: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.width(80.dp)
    ) {
        Card(
            modifier = Modifier.size(64.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(24.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
fun OverviewStatItem(label: String, value: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier.size(32.dp).background(iconColor.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
