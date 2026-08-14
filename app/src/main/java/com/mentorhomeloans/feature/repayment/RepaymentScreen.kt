package com.mentorhomeloans.feature.repayment

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.core.utils.CurrencyUtils
import com.mentorhomeloans.domain.model.LoanRepaymentDetail

// ── colour constants ──────────────────────────────────────────────────────────
private val PaidGreen   = Color(0xFF2E7D32)
private val PaidGreenBg = Color(0xFFE8F5E9)
private val OverdueRed  = Color(0xFFC62828)
private val OverdueRedBg = Color(0xFFFFEBEE)
private val CardBg      = Color(0xFFF8F9FE)
private val SummaryGrad = listOf(Color(0xFF1565C0), Color(0xFF1E88E5))

/**
 * Repayment screen that calls GetLoanRepaymentDetails and displays all
 * API fields in a clean, readable list.
 */
@Composable
fun RepaymentScreen(
    navController: NavController,
    viewModel: RepaymentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Repayment Schedule",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CardBg)
        ) {
            when (val state = uiState) {
                is RepaymentUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is RepaymentUIState.Success -> {
                    val items = state.items
                    val paidCount    = items.count { it.emiStatus.equals("Paid", ignoreCase = true) }
                    val overdueCount = items.count { it.emiStatus.equals("Overdue", ignoreCase = true) }
                    val totalPrincipalPaid = items.firstOrNull()?.totalPrinciplePaid ?: 0.0
                    val totalEmiAmount = items.sumOf { it.emiAmount }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // ── Summary header card ───────────────────────────────
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SummaryHeaderCard(
                                totalPrincipalPaid = totalPrincipalPaid,
                                totalEmiAmount = totalEmiAmount,
                                totalPeriods = items.size,
                                paidCount = paidCount,
                                overdueCount = overdueCount
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Stats row ─────────────────────────────────────
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatChip(
                                    modifier = Modifier.weight(1f),
                                    label = "Paid",
                                    value = "$paidCount",
                                    color = PaidGreen,
                                    bgColor = PaidGreenBg
                                )
                                StatChip(
                                    modifier = Modifier.weight(1f),
                                    label = "Overdue",
                                    value = "$overdueCount",
                                    color = OverdueRed,
                                    bgColor = OverdueRedBg
                                )
                                StatChip(
                                    modifier = Modifier.weight(1f),
                                    label = "Total EMIs",
                                    value = "${items.size}",
                                    color = Color(0xFF1565C0),
                                    bgColor = Color(0xFFE3F2FD)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "EMI Schedule",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // ── EMI list ──────────────────────────────────────────
                        itemsIndexed(items) { index, item ->
                            RepaymentListItem(item = item, index = index)
                            if (index < items.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    color = Color(0xFFE0E0E0),
                                    thickness = 0.5.dp
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }

                is RepaymentUIState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadRepaymentDetails() }
                    )
                }
            }
        }
    }
}

// ── Summary Header Card ───────────────────────────────────────────────────────

@Composable
private fun SummaryHeaderCard(
    totalPrincipalPaid: Double,
    totalEmiAmount: Double,
    totalPeriods: Int,
    paidCount: Int,
    overdueCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(SummaryGrad))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Total Principal Paid",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = CurrencyUtils.formatINR(totalPrincipalPaid),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    SummarySubItem(label = "Total EMI Value", value = CurrencyUtils.formatINR(totalEmiAmount))
                    SummarySubItem(label = "EMI Periods", value = "$totalPeriods")
                }
            }
        }
    }
}

@Composable
private fun SummarySubItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

// ── Stat Chip ─────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

// ── Single EMI Row ────────────────────────────────────────────────────────────

@Composable
private fun RepaymentListItem(item: LoanRepaymentDetail, index: Int) {
    val isPaid    = item.emiStatus.equals("Paid", ignoreCase = true)
    val isOverdue = item.emiStatus.equals("Overdue", ignoreCase = true)

    val statusColor by animateColorAsState(
        targetValue = when {
            isPaid    -> PaidGreen
            isOverdue -> OverdueRed
            else      -> Color(0xFF616161)
        },
        animationSpec = tween(300),
        label = "statusColor"
    )

    val statusBg by animateColorAsState(
        targetValue = when {
            isPaid    -> PaidGreenBg
            isOverdue -> OverdueRedBg
            else      -> Color(0xFFF5F5F5)
        },
        animationSpec = tween(300),
        label = "statusBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Period badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isPaid) PaidGreenBg else if (isOverdue) OverdueRedBg else Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.period}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Due date + principal paid
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatEmiDate(item.emiDueDate),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Principal Paid: ${CurrencyUtils.formatINR(item.totalPrinciplePaid)}",
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Amount + status badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = CurrencyUtils.formatINR(item.emiAmount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = item.emiStatus,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }
        }
    }
}

/**
 * Formats the server date string "MM/dd/yyyy HH:mm:ss" → "dd MMM yyyy".
 * Returns the original string if parsing fails.
 */
private fun formatEmiDate(raw: String): String {
    return try {
        val inputFormat  = java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US)
        val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US)
        val date = inputFormat.parse(raw) ?: return raw
        outputFormat.format(date)
    } catch (e: Exception) {
        raw
    }
}
