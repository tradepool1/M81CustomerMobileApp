package com.mentorhomeloans.feature.transactions

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.model.TransactionStatus
import com.mentorhomeloans.domain.model.TransactionType
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.core.ui.components.EmptyState
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import com.mentorhomeloans.core.utils.DateUtils
import java.util.Calendar
import com.mentorhomeloans.ui.theme.MentorBlue

/**
 * Enhanced Transactions screen with type filter chips, date range, and rich transaction cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val dateFrom by viewModel.dateFrom.collectAsState()
    val dateTo by viewModel.dateTo.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val filterTypes = listOf(
        null to "All",
        TransactionType.EMI_PAYMENT to "EMI",
        TransactionType.PREPAYMENT to "Prepayment",
        TransactionType.PENAL_CHARGE to "Penal Charge",
        TransactionType.BOUNCE_CHARGE to "Bounce Charge"
    )

    val hasFilters = viewModel.hasActiveFilters()

    // Date picker helpers
    fun showFromDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            viewModel.setDateRange(String.format("%04d-%02d-%02d", y, m + 1, d), dateTo)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showToDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            viewModel.setDateRange(dateFrom, String.format("%04d-%02d-%02d", y, m + 1, d))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Transaction History",
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchTransactions(it)
                },
                placeholder = { Text("Search by description or ref no...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MentorBlue,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            // Type Filter Chips (horizontal scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTypes.forEach { (type, label) ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTypeFilter(type) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MentorBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Date range chip
                FilterChip(
                    selected = dateFrom != null || dateTo != null,
                    onClick = { showDateRangePicker = !showDateRangePicker },
                    label = { Text("Date Range", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, null, Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MentorBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Date Range Picker Row
            AnimatedVisibility(
                visible = showDateRangePicker,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showFromDatePicker() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(dateFrom ?: "From Date", fontSize = 12.sp, maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = { showToDatePicker() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(dateTo ?: "To Date", fontSize = 12.sp, maxLines = 1)
                    }
                }
            }

            // Active filter banner
            if (hasFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Filters active",
                        fontSize = 12.sp,
                        color = MentorBlue,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        onClick = {
                            viewModel.clearFilters()
                            showDateRangePicker = false
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Clear All", fontSize = 12.sp, color = Color(0xFFD84315))
                    }
                }
            }

            // Content
            when (val state = uiState) {
                is TransactionsUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TransactionsUIState.Success -> {
                    if (state.transactions.isEmpty()) {
                        EmptyState(message = "No transactions found for the selected filters.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.transactions) { item ->
                                TransactionCard(transaction = item)
                            }
                        }
                    }
                }
                is TransactionsUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadTransactions() })
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    var expanded by remember { mutableStateOf(false) }

    val typeColor = when (transaction.type) {
        TransactionType.EMI_PAYMENT -> MentorBlue
        TransactionType.PREPAYMENT, TransactionType.PART_PAYMENT -> Color(0xFF43A047)
        TransactionType.PENAL_CHARGE, TransactionType.BOUNCE_CHARGE,
        TransactionType.OTHER_CHARGE, TransactionType.FORECLOSURE_CHARGE -> Color(0xFFE53935)
        else -> Color(0xFF757575)
    }

    val typeBgColor = typeColor.copy(alpha = 0.1f)

    val statusColor = when (transaction.status) {
        TransactionStatus.SUCCESS -> Color(0xFF43A047)
        TransactionStatus.PENDING -> Color(0xFFF9A825)
        TransactionStatus.BOUNCED, TransactionStatus.FAILED -> Color(0xFFE53935)
        TransactionStatus.REVERSED -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
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
                // Left: type badge + description
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(typeBgColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = transaction.type.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeColor
                        )
                    }
                }

                // Right: status dot + status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        transaction.status.displayName,
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = DateUtils.formatDisplayDate(transaction.transactionDate),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Ref: ${transaction.referenceNumber}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyUtils.formatINR(transaction.amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = transaction.paymentMode.displayName,
                        fontSize = 11.sp,
                        color = MentorBlue
                    )
                }
            }

            // Expandable breakdown
            AnimatedVisibility(visible = expanded) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray)
                    Text("Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        BreakdownItem("Principal", CurrencyUtils.formatINR(transaction.principalComponent), MentorBlue)
                        BreakdownItem("Interest", CurrencyUtils.formatINR(transaction.interestComponent), Color(0xFFFF9800))
                        BreakdownItem("Charges", CurrencyUtils.formatINR(transaction.chargesComponent), Color(0xFFE53935))
                    }
                    if (!transaction.balance.equals(0.0)) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Outstanding Balance", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                CurrencyUtils.formatINR(transaction.balance),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Expand/collapse hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BreakdownItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
