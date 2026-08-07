package com.mentorhomeloans.feature.statements

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.ui.theme.MentorBlue
import java.util.Calendar

/**
 * Enhanced Statements screen: period selector chips, statement list, and documents section.
 */
@Composable
fun StatementsScreen(
    navController: NavController,
    viewModel: StatementsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val context = LocalContext.current
    var showCustomRangeDialog by remember { mutableStateOf(false) }

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
                title = "Statements & Documents",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FE)),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Section A: Loan Statements ──────────────────────────────────
            item {
                SectionHeader(title = "Loan Statements", icon = Icons.Default.Description)
            }

            // Period selector chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 4.dp),
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
            }

            when (val state = uiState) {
                is StatementsUIState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is StatementsUIState.Success -> {
                    if (state.statements.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No statements available for this period.",
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

                    // ── Section B: Documents ──────────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(title = "Documents", icon = Icons.Default.Folder)
                    }

                    // Loan Statement (all)
                    item {
                        DocumentCard(
                            title = "Loan Statement",
                            subtitle = "Full account statement (PDF/CSV)",
                            icon = Icons.Default.Article,
                            iconColor = MentorBlue,
                            canDownload = viewModel.adminAllowsDownload,
                            onDownloadPdf = {
                                state.allStatements.firstOrNull()?.let {
                                    viewModel.downloadStatement(it, isPdf = true)
                                } ?: Toast.makeText(context, "No statements available.", Toast.LENGTH_SHORT).show()
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
                            onDownloadCsv = null // Interest cert is PDF only
                        )
                    }

                    // Repayment Schedule / Amortization (only if admin allows)
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
                is StatementsUIState.Error -> {
                    item {
                        ErrorState(
                            message = state.message,
                            onRetry = { viewModel.loadStatements() }
                        )
                    }
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

    var startMonthIdx by remember { mutableStateOf(0) }   // 0-based index
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
                // ── Title ──
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

                // ── Start ──────────────────────────────────────────
                Text(
                    "From",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF444444)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Start Month
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
                    // Start Year
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

                // ── End ────────────────────────────────────────────
                Text(
                    "To",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF444444)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // End Month
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
                    // End Year
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

                // Validation error
                if (errorText.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(errorText, fontSize = 12.sp, color = Color(0xFFE53935))
                }

                Spacer(Modifier.height(24.dp))

                // ── Action Buttons ──
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

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
