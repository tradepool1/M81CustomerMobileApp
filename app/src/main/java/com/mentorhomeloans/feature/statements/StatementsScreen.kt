package com.mentorhomeloans.feature.statements

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import com.mentorhomeloans.domain.model.LoanSOADetail
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.ui.theme.MentorBlue
import java.util.Calendar

/**
 * Enhanced Statements & SOA Screen displaying Statement of Account ledger details,
 * summary metrics, search bar, period filters, and downloadable documents.
 */
@Composable
fun StatementsScreen(
    navController: NavController,
    viewModel: StatementsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    var showCustomRangeDialog by remember { mutableStateOf(false) }

    // Selected tab state: 0 = SOA Ledger, 1 = Documents & Downloads
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        viewModel.downloadEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    // Custom Range Picker Dialog
    if (showCustomRangeDialog) {
        MonthYearRangePickerDialog(
            onDismiss = { showCustomRangeDialog = false },
            onApply = { start, end ->
                showCustomRangeDialog = false
                viewModel.applyCustomRange(start, end)
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Statement of Account",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FE))
        ) {



            // ── Tab Selection Row ──────────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MentorBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("SOA Ledger", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Documents", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
            }

            // ── Period Filter Chips ───────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PeriodFilter.values().forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = {
                            if (period == PeriodFilter.CUSTOM) {
                                viewModel.selectPeriod(period)
                                showCustomRangeDialog = true
                            } else {
                                viewModel.selectPeriod(period)
                            }
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(period.label, fontSize = 12.sp)
                                if (period == PeriodFilter.CUSTOM) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MentorBlue,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            when (val state = uiState) {
                is StatementsUIState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MentorBlue)
                            Spacer(Modifier.height(12.dp))
                            Text("Fetching Statement of Account...", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
                is StatementsUIState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorState(
                            message = state.message,
                            onRetry = { viewModel.loadStatements() }
                        )
                    }
                }
                is StatementsUIState.Success -> {
                    if (selectedTab == 0) {
                        // ──────────────────────────────────────────────────────
                        // TAB 0: Statement of Account (SOA Ledger)
                        // ──────────────────────────────────────────────────────
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            // Aggregate Summary Metric Cards
                            item {
                                MetricsSummaryCard(
                                    totalDebit = state.totalDebit,
                                    totalCredit = state.totalCredit,
                                    netBalance = state.netBalance
                                )
                            }

                            // Search Bar
                            item {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChange(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    placeholder = { Text("Search transactions, receipt #, or particulars...", fontSize = 13.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MentorBlue,
                                        unfocusedBorderColor = Color(0xFFDDDDDD),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                )
                            }

                            // SOA Transaction Item Count Header
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Transactions (${state.filteredSoaDetails.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MentorBlue
                                    )
                                    if (state.filteredSoaDetails.isNotEmpty()) {
                                        Text(
                                            text = "Chronological Order",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            // Empty State
                            if (state.filteredSoaDetails.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FindInPage,
                                                contentDescription = null,
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(56.dp)
                                            )
                                            Spacer(Modifier.height(16.dp))
                                            Text(
                                                "No Transactions Found",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.DarkGray
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            Text(
                                                when {
                                                    searchQuery.isNotEmpty() ->
                                                        "No records match your search \"$searchQuery\".\nTry a different keyword."
                                                    selectedPeriod == PeriodFilter.CURRENT_MONTH ->
                                                        "No transactions found for the current month.\nTry selecting 'All' or a custom date range."
                                                    selectedPeriod == PeriodFilter.SIX_MONTHS ->
                                                        "No transactions found in the last 6 months.\nTry 'All' or a custom date range to view historical data."
                                                    selectedPeriod == PeriodFilter.FINANCIAL_YEAR ->
                                                        "No transactions found for the current financial year.\nTry 'All' or a custom date range to view older records."
                                                    selectedPeriod == PeriodFilter.CUSTOM ->
                                                        "No transactions found for the selected date range.\nTry adjusting the range."
                                                    else ->
                                                        "No transaction records available."
                                                },
                                                fontSize = 13.sp,
                                                color = Color.Gray,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(state.filteredSoaDetails) { item ->
                                    SOATransactionCard(item = item)
                                }
                            }
                        }
                    } else {
                        // ──────────────────────────────────────────────────────
                        // TAB 1: Documents & Downloadable Files
                        // ──────────────────────────────────────────────────────
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                SectionHeader(title = "Account Statements", icon = Icons.Default.Description)
                            }

                            if (state.statements.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No document statements available for this period.",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            } else {
                                items(state.statements) { statement ->
                                    StatementCard(
                                        statement = statement,
                                        canDownload = viewModel.adminAllowsDownload,
                                        onDownloadPdf = { viewModel.downloadStatement(statement, isPdf = true) },
                                        onDownloadCsv = { viewModel.downloadStatement(statement, isPdf = false) }
                                    )
                                }
                            }

                            item {
                                Spacer(Modifier.height(12.dp))
                                SectionHeader(title = "Certificates & Schedules", icon = Icons.Default.Folder)
                            }

                            // Full Account Statement PDF/CSV
                            item {
                                DocumentCard(
                                    title = "Full Statement of Account (SOA)",
                                    subtitle = "Complete account ledger in PDF or CSV format",
                                    icon = Icons.Default.Article,
                                    iconColor = MentorBlue,
                                    canDownload = viewModel.adminAllowsDownload,
                                    onDownloadPdf = {
                                        state.allStatements.firstOrNull()?.let {
                                            viewModel.downloadStatement(it, isPdf = true)
                                        } ?: Toast.makeText(context, "SOA statement queued for download.", Toast.LENGTH_SHORT).show()
                                    },
                                    onDownloadCsv = {
                                        state.allStatements.firstOrNull()?.let {
                                            viewModel.downloadStatement(it, isPdf = false)
                                        }
                                    }
                                )
                            }

                            // Interest Certificate
                            item {
                                DocumentCard(
                                    title = "Interest Certificate",
                                    subtitle = "For tax deduction under Section 24(b)",
                                    icon = Icons.Default.RequestPage,
                                    iconColor = Color(0xFF43A047),
                                    canDownload = viewModel.adminAllowsDownload,
                                    onDownloadPdf = { viewModel.downloadInterestCertificate() },
                                    onDownloadCsv = null
                                )
                            }

                            // Repayment Schedule / Amortization
                            if (viewModel.adminAllowsAmortization) {
                                item {
                                    DocumentCard(
                                        title = "Repayment Schedule",
                                        subtitle = "Full amortization table (Instalment breakup)",
                                        icon = Icons.Default.TableChart,
                                        iconColor = Color(0xFF8E24AA),
                                        canDownload = viewModel.adminAllowsDownload,
                                        onDownloadPdf = { viewModel.downloadAmortizationSchedule() },
                                        onDownloadCsv = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Metrics Summary Header Card ───────────────────────────────────────────────
@Composable
private fun MetricsSummaryCard(
    totalDebit: Double,
    totalCredit: Double,
    netBalance: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ledger Summary",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Debit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFFE53935), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.width(6.dp))
                            Text("Total Debit", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = CurrencyUtils.formatINR(totalDebit),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }

                // Total Credit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFF43A047), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.width(6.dp))
                            Text("Total Credit", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = CurrencyUtils.formatINR(totalCredit),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                // Net Balance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(MentorBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.width(6.dp))
                            Text("Net Balance", fontSize = 11.sp, color = MentorBlue, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = CurrencyUtils.formatINR(netBalance),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MentorBlue
                        )
                    }
                }
            }
        }
    }
}

// ── SOA Transaction Item Card ─────────────────────────────────────────────────
@Composable
private fun SOATransactionCard(item: LoanSOADetail) {
    var expanded by remember { mutableStateOf(false) }

    // Category / Badge helper
    val isCredit = item.credit > 0
    val isDebit = item.debit > 0

    val badgeText = when {
        item.particular.contains("Amount Received", ignoreCase = true) || item.particular.contains("Receipt", ignoreCase = true) -> "Payment Received"
        item.particular.contains("Amount Paid", ignoreCase = true) -> "Amount Disbursed"
        item.particular.contains("Installment", ignoreCase = true) -> "EMI Due"
        item.particular.contains("Interest Waive Off", ignoreCase = true) -> "Interest Waiver"
        item.particular.contains("Fee Due", ignoreCase = true) -> "Fee Due"
        item.particular.contains("Payable", ignoreCase = true) -> "Payable"
        isCredit -> "Credit"
        else -> "Debit"
    }

    val badgeBg = when {
        isCredit -> Color(0xFFE8F5E9)
        badgeText == "Amount Disbursed" -> Color(0xFFFFF3E0)
        else -> Color(0xFFFFEBEE)
    }

    val badgeTextColor = when {
        isCredit -> Color(0xFF2E7D32)
        badgeText == "Amount Disbursed" -> Color(0xFFE65100)
        else -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Voucher Date & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MentorBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = item.voucherDate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Middle Section: Particulars description
            Text(
                text = item.particular,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.particular.length > 80 && !expanded) {
                Text(
                    text = "Tap to see full details...",
                    fontSize = 10.sp,
                    color = MentorBlue,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            // Bottom Section: Debit, Credit & Balance Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Debit Amount
                Column {
                    Text("Debit", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = if (item.debit > 0) CurrencyUtils.formatINR(item.debit) else "—",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.debit > 0) Color(0xFFD32F2F) else Color.Gray
                    )
                }

                // Credit Amount
                Column {
                    Text("Credit", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = if (item.credit > 0) CurrencyUtils.formatINR(item.credit) else "—",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.credit > 0) Color(0xFF388E3C) else Color.Gray
                    )
                }

                // Balance
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = CurrencyUtils.formatINR(item.balance),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MentorBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(20.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
    }
}

@Composable
private fun StatementCard(
    statement: Statement,
    canDownload: Boolean,
    onDownloadPdf: () -> Unit,
    onDownloadCsv: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(statement.period, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${statement.fromDate}  →  ${statement.toDate}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(statement.type.displayName, fontSize = 10.sp, color = MentorBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total Paid", fontSize = 10.sp, color = Color.Gray)
                    Text(CurrencyUtils.formatINR(statement.totalPaid), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Closing Balance", fontSize = 10.sp, color = Color.Gray)
                    Text(CurrencyUtils.formatINR(statement.closingBalance), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Size", fontSize = 10.sp, color = Color.Gray)
                    Text("${statement.sizeKb} KB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (canDownload) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDownloadPdf,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("PDF", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDownloadCsv,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF43A047))
                    ) {
                        Icon(Icons.Default.TableRows, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CSV", fontSize = 12.sp)
                    }
                }
            } else {
                Text(
                    "View only – downloads not permitted by your admin.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DocumentCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    canDownload: Boolean,
    onDownloadPdf: () -> Unit,
    onDownloadCsv: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(10.dp))
                if (canDownload) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onDownloadPdf,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                        ) {
                            Icon(Icons.Default.PictureAsPdf, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp)
                        }
                        if (onDownloadCsv != null) {
                            OutlinedButton(
                                onClick = onDownloadCsv,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF43A047))
                            ) {
                                Icon(Icons.Default.TableRows, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("CSV", fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    Text("View only – downloads disabled by admin.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

// ── Custom Month-Year Range Picker Dialog ─────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearRangePickerDialog(
    onDismiss: () -> Unit,
    onApply: (start: StatementsViewModel.MonthYear, end: StatementsViewModel.MonthYear) -> Unit
) {
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo currentYear - 9).toList()

    var startMonthIdx by remember { mutableStateOf(0) }
    var startYear     by remember { mutableStateOf(currentYear) }
    var endMonthIdx   by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var endYear       by remember { mutableStateOf(currentYear) }

    var startMonthExpanded by remember { mutableStateOf(false) }
    var startYearExpanded  by remember { mutableStateOf(false) }
    var endMonthExpanded   by remember { mutableStateOf(false) }
    var endYearExpanded    by remember { mutableStateOf(false) }

    var errorText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MentorBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Custom Date Range",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MentorBlue
                        )
                        Text(
                            "Select start and end month & year",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("From", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1.6f)) {
                        ExposedDropdownMenuBox(
                            expanded = startMonthExpanded,
                            onExpandedChange = { startMonthExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = monthNames[startMonthIdx],
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Month", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(startMonthExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFDDDDDD)
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = startMonthExpanded,
                                onDismissRequest = { startMonthExpanded = false }
                            ) {
                                monthNames.forEachIndexed { idx, name ->
                                    DropdownMenuItem(
                                        text = { Text(name, fontSize = 13.sp) },
                                        onClick = { startMonthIdx = idx; startMonthExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = startYearExpanded,
                            onExpandedChange = { startYearExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = startYear.toString(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Year", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(startYearExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFDDDDDD)
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = startYearExpanded,
                                onDismissRequest = { startYearExpanded = false }
                            ) {
                                years.forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text(yr.toString(), fontSize = 13.sp) },
                                        onClick = { startYear = yr; startYearExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("To", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1.6f)) {
                        ExposedDropdownMenuBox(
                            expanded = endMonthExpanded,
                            onExpandedChange = { endMonthExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = monthNames[endMonthIdx],
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Month", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(endMonthExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFDDDDDD)
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = endMonthExpanded,
                                onDismissRequest = { endMonthExpanded = false }
                            ) {
                                monthNames.forEachIndexed { idx, name ->
                                    DropdownMenuItem(
                                        text = { Text(name, fontSize = 13.sp) },
                                        onClick = { endMonthIdx = idx; endMonthExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = endYearExpanded,
                            onExpandedChange = { endYearExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = endYear.toString(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Year", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(endYearExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFDDDDDD)
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = endYearExpanded,
                                onDismissRequest = { endYearExpanded = false }
                            ) {
                                years.forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text(yr.toString(), fontSize = 13.sp) },
                                        onClick = { endYear = yr; endYearExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }

                if (errorText.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(errorText, fontSize = 12.sp, color = Color(0xFFE53935))
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            val startVal = startYear * 100 + (startMonthIdx + 1)
                            val endVal   = endYear   * 100 + (endMonthIdx + 1)
                            if (endVal < startVal) {
                                errorText = "End date must be after start date"
                            } else {
                                errorText = ""
                                onApply(
                                    StatementsViewModel.MonthYear(startMonthIdx + 1, startYear),
                                    StatementsViewModel.MonthYear(endMonthIdx + 1, endYear)
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MentorBlue)
                    ) {
                        Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
