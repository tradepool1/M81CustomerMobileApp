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
                                val prodName = if (loan.productName.isNotBlank()) loan.productName else loan.loanType.displayName
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(prodName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MentorBlue)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Status: ${loan.status.displayName}", fontSize = 14.sp, color = Color(0xFF00BFA5), fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Financial Breakdown Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Financial Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Loan Amount", CurrencyUtils.formatINR(loan.sanctionAmount))
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Disbursement Amount", CurrencyUtils.formatINR(loan.disbursedAmount))
                                if (loan.netFinance > 0) {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("Net Finance", CurrencyUtils.formatINR(loan.netFinance))
                                }
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Outstanding (POS)", CurrencyUtils.formatINR(loan.outstandingAmount))
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Interest Rate (IRR)", CurrencyUtils.formatInterestRate(loan.interestRate))
                            }
                        }

                        // Tenure Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Tenure Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Total Loan Tenure", "${loan.tenure} Months")
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Received Tenure", "${loan.receivedTenure} Month${if (loan.receivedTenure != 1) "s" else ""}")
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("Remaining Tenure", "${loan.remainingTenure} Month${if (loan.remainingTenure != 1) "s" else ""}")
                            }
                        }

                        // Payment & Collection Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Repayment Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                HorizontalDivider(color = Color.LightGray)
                                DetailRow("EMI Amount", CurrencyUtils.formatINR(loan.emiAmount))
                                if (loan.hasNextEmiDate) {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("EMI Due Date", loan.nextEmiDate)
                                } else {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("Maturity Date", if (loan.maturityDate.isNotBlank()) loan.maturityDate else "Loan Matured")
                                }
                                if (loan.principlReceived > 0) {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("Principal Received", CurrencyUtils.formatINR(loan.principlReceived))
                                }
                                if (loan.interestReceived > 0) {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("Interest Received", CurrencyUtils.formatINR(loan.interestReceived))
                                }
                                if (loan.receivedAmt > 0) {
                                    HorizontalDivider(color = Color.LightGray)
                                    DetailRow("Total Received Amount", CurrencyUtils.formatINR(loan.receivedAmt))
                                }
                            }
                        }
                    }
                }
                is LoanDetailsUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadDetails() })
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
