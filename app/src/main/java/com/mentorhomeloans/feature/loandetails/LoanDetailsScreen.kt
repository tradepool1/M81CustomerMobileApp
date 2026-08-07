package com.mentorhomeloans.feature.loandetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import com.mentorhomeloans.ui.theme.MentorBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailsScreen(
    navController: NavController,
    viewModel: LoanDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Loan Details", fontWeight = FontWeight.Bold, color = MentorBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MentorBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FE))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FE))
        ) {
            when (val state = uiState) {
                is LoanDetailsUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoanDetailsUIState.Success -> {
                    val loan = state.loan
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Account Number", fontSize = 12.sp, color = Color.Gray)
                                Text(loan.accountNumber, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Status: ${loan.status.displayName}", fontSize = 14.sp, color = Color(0xFF00BFA5), fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Loan Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DetailRow("Loan Type", loan.loanType.displayName)
                                Divider(color = Color.LightGray)
                                DetailRow("Sanction Amount", CurrencyUtils.formatINR(loan.sanctionAmount))
                                Divider(color = Color.LightGray)
                                DetailRow("Disbursed Amount", CurrencyUtils.formatINR(loan.disbursedAmount))
                                Divider(color = Color.LightGray)
                                DetailRow("Outstanding Amount", CurrencyUtils.formatINR(loan.outstandingAmount))
                                Divider(color = Color.LightGray)
                                DetailRow("Interest Rate", "${loan.interestRate}% p.a.")
                            }
                        }

                        // Repayment Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DetailRow("Tenure", "${loan.tenure} months")
                                Divider(color = Color.LightGray)
                                DetailRow("Remaining Tenure", "${loan.remainingTenure} months")
                                Divider(color = Color.LightGray)
                                DetailRow("EMI Amount", CurrencyUtils.formatINR(loan.emiAmount))
                                Divider(color = Color.LightGray)
                                DetailRow("Next EMI Date", loan.nextEmiDate)
                                Divider(color = Color.LightGray)
                                DetailRow("Overdue Status", if (loan.isOverdue) "Overdue (${CurrencyUtils.formatINR(loan.overdueAmount)})" else "On Track")
                            }
                        }

                        // Dates & Management Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DetailRow("Start Date", loan.startDate)
                                Divider(color = Color.LightGray)
                                DetailRow("Maturity Date", loan.maturityDate)
                                Divider(color = Color.LightGray)
                                DetailRow("Branch", loan.branchName)
                                Divider(color = Color.LightGray)
                                DetailRow("Loan Manager", loan.loanManagerName)
                            }
                        }
                    }
                }
                is LoanDetailsUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { /* Handle retry or navigate back */ })
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}
