package com.mentorhomeloans.feature.support

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.domain.model.SLAStatus
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.model.TicketAttachment
import com.mentorhomeloans.domain.model.TicketCategory
import com.mentorhomeloans.domain.model.TicketPriority
import com.mentorhomeloans.domain.model.TicketStatus
import com.mentorhomeloans.ui.theme.MentorBlue

/**
 * Enhanced Support / Grievance Module Screen featuring Admin Configurable Raise Request button,
 * categorized ticket creation, SLA resolution tracking, document attachments, and thread detail view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    navController: NavController,
    viewModel: SupportViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showRaiseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.ticketRaisedEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Support & Grievance",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            if (uiState is SupportUIState.Success) {
                val state = uiState as SupportUIState.Success
                ExtendedFloatingActionButton(
                    onClick = {
                        if (state.isAdminRaiseAllowed) {
                            showRaiseDialog = true
                        } else {
                            Toast.makeText(context, "Ticket creation is currently disabled by Admin.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = if (state.isAdminRaiseAllowed) MentorBlue else Color.Gray,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Raise Request") },
                    text = { Text("Raise Request", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF4F6F9))
        ) {
            when (val state = uiState) {
                is SupportUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MentorBlue)
                    }
                }
                is SupportUIState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 1. Metrics & Admin Control Header
                        SupportMetricsHeader(
                            tickets = state.tickets,
                            isAdminAllowed = state.isAdminRaiseAllowed,
                            onToggleAdmin = { viewModel.toggleAdminRaiseConfig() },
                            onRaiseClick = {
                                if (state.isAdminRaiseAllowed) showRaiseDialog = true
                                else Toast.makeText(context, "Ticket creation disabled by Admin.", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // 2. Filter Tabs
                        FilterTabRow(
                            selectedTab = state.selectedFilter,
                            onTabSelected = { viewModel.setFilterTab(it) }
                        )

                        // 3. Ticket List
                        if (state.filteredTickets.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = null,
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No tickets found in this section",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                            ) {
                                items(state.filteredTickets, key = { it.id }) { ticket ->
                                    TicketItemCard(
                                        ticket = ticket,
                                        onClick = { viewModel.selectTicket(ticket) }
                                    )
                                }
                            }
                        }
                    }

                    // Ticket Creation Dialog
                    if (showRaiseDialog) {
                        RaiseGrievanceDialog(
                            onDismiss = { showRaiseDialog = false },
                            onSubmit = { category, subject, description, priority, attachments, loanAccount ->
                                viewModel.raiseTicket(category, subject, description, attachments, priority, loanAccount)
                                showRaiseDialog = false
                            }
                        )
                    }

                    // Ticket Details Bottom Sheet / Dialog
                    state.selectedTicket?.let { ticket ->
                        TicketDetailDialog(
                            ticket = ticket,
                            onDismiss = { viewModel.selectTicket(null) },
                            onSendReply = { msg ->
                                viewModel.sendReply(ticket.id, msg)
                            }
                        )
                    }
                }
                is SupportUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadTickets() })
                }
            }
        }
    }
}

/**
 * Top banner displaying metric stats and the Admin toggle bar for Raise Request capability.
 */
@Composable
fun SupportMetricsHeader(
    tickets: List<SupportTicket>,
    isAdminAllowed: Boolean,
    onToggleAdmin: () -> Unit,
    onRaiseClick: () -> Unit
) {
    val totalOpen = tickets.count { it.status == TicketStatus.OPEN }
    val totalInProgress = tickets.count { it.status == TicketStatus.IN_PROGRESS || it.status == TicketStatus.AWAITING_CUSTOMER }
    val totalResolved = tickets.count { it.status == TicketStatus.RESOLVED || it.status == TicketStatus.CLOSED }
    val breachRiskCount = tickets.count { it.slaStatus == SLAStatus.BREACH_RISK || it.slaStatus == SLAStatus.BREACHED }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MentorBlue, MentorBlue)
                )
            )
            .padding(16.dp)
    ) {
        // Admin Config Toggle Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Setting",
                    tint = Color(0xFFF9A01B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Admin Configuration",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAdminAllowed) "Raise Request: Enabled" else "Raise Request: Disabled by Admin",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = isAdminAllowed,
                onCheckedChange = { onToggleAdmin() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MentorBlue,
                    checkedTrackColor = Color(0xFFF9A01B),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                ),
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricCard(
                title = "Open Tickets",
                value = "$totalOpen",
                subtitle = "$totalInProgress In Progress",
                icon = Icons.Default.PendingActions,
                iconBg = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard(
                title = "SLA Alerts",
                value = "$breachRiskCount",
                subtitle = "Near SLA breach",
                icon = Icons.Outlined.Timer,
                iconBg = if (breachRiskCount > 0) Color(0xFFE53935) else Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard(
                title = "Resolved",
                value = "$totalResolved",
                subtitle = "Closed grievances",
                icon = Icons.Default.CheckCircle,
                iconBg = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prominent Raise Request Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRaiseClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (isAdminAllowed) Color(0xFFF9A01B) else Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddComment,
                        contentDescription = null,
                        tint = if (isAdminAllowed) MentorBlue else Color.DarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Raise Request / Grievance",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdminAllowed) MentorBlue else Color.DarkGray
                        )
                        Text(
                            text = if (isAdminAllowed) "Statement, Payment, Loan & Contact queries" else "Disabled by Administrator",
                            fontSize = 11.sp,
                            color = if (isAdminAllowed) MentorBlue.copy(alpha = 0.8f) else Color.DarkGray
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = if (isAdminAllowed) MentorBlue else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(iconBg.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
            Text(text = subtitle, fontSize = 9.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/**
 * Filter tabs row: All, Open, In Progress, Resolved.
 */
@Composable
fun FilterTabRow(
    selectedTab: TicketFilterTab,
    onTabSelected: (TicketFilterTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 16.dp,
        containerColor = Color.White,
        contentColor = MentorBlue,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                color = MentorBlue,
                height = 3.dp
            )
        }
    ) {
        TicketFilterTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            )
        }
    }
}

/**
 * Card representing an individual Support / Grievance ticket.
 */
@Composable
fun TicketItemCard(
    ticket: SupportTicket,
    onClick: () -> Unit
) {
    val categoryIcon = getCategoryIcon(ticket.category)
    val slaColor = when (ticket.slaStatus) {
        SLAStatus.ON_TRACK -> Color(0xFF2E7D32)
        SLAStatus.BREACH_RISK -> Color(0xFFED6C02)
        SLAStatus.BREACHED -> Color(0xFFD32F2F)
        SLAStatus.RESOLVED_ON_TIME -> Color(0xFF0288D1)
    }

    val statusBg = when (ticket.status) {
        TicketStatus.OPEN -> Color(0xFFFFF3E0)
        TicketStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
        TicketStatus.AWAITING_CUSTOMER -> Color(0xFFFFF8E1)
        TicketStatus.RESOLVED -> Color(0xE8E8F5E9)
        TicketStatus.CLOSED -> Color(0xFFEEEEEE)
        TicketStatus.REOPENED -> Color(0xFFFBE9E7)
    }

    val statusColor = when (ticket.status) {
        TicketStatus.OPEN -> Color(0xFFEF6C00)
        TicketStatus.IN_PROGRESS -> Color(0xFF1976D2)
        TicketStatus.AWAITING_CUSTOMER -> Color(0xFFF57F17)
        TicketStatus.RESOLVED -> Color(0xFF2E7D32)
        TicketStatus.CLOSED -> Color(0xFF616161)
        TicketStatus.REOPENED -> Color(0xFFD84315)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Ticket #, Category Pill & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MentorBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = categoryIcon, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = ticket.ticketNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                        Text(text = ticket.category.displayName, fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = ticket.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject
            Text(
                text = ticket.subject,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description snippet
            Text(
                text = ticket.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // SLA Tracking & Attachments Info Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SLA Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "SLA Target",
                        tint = slaColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SLA (${ticket.slaHours}h): ${ticket.slaStatus.displayName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = slaColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (ticket.attachments.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attachments",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${ticket.attachments.size} file(s)",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "View Details →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MentorBlue
                    )
                }
            }
        }
    }
}

/**
 * Dialog form for creating a new Grievance / Support Ticket.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseGrievanceDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        category: TicketCategory,
        subject: String,
        description: String,
        priority: TicketPriority,
        attachments: List<String>,
        loanAccount: String?
    ) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(TicketCategory.STATEMENT_ISSUE) }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var attachedFiles by remember { mutableStateOf(mutableListOf<String>()) }
    var loanAccount by remember { mutableStateOf("MHL-2024-88492") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = null,
                            tint = MentorBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Raise Support Request",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MentorBlue
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Category Selection
                    Text(text = "Select Category *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf(
                        TicketCategory.STATEMENT_ISSUE,
                        TicketCategory.PAYMENT_ISSUE,
                        TicketCategory.LOAN_QUERY,
                        TicketCategory.TECHNICAL_ISSUE,
                        TicketCategory.CONTACT_ADDRESS_CHANGE,
                        TicketCategory.GENERAL_INQUIRY,
                        TicketCategory.OTHER
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowCategories.forEach { cat ->
                                    val isSelected = selectedCategory == cat
                                    OutlinedCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedCategory = cat },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MentorBlue else Color.LightGray
                                        ),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = if (isSelected) MentorBlue.copy(alpha = 0.08f) else Color.White
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(cat),
                                                contentDescription = null,
                                                tint = if (isSelected) MentorBlue else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = cat.displayName,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MentorBlue else Color.DarkGray,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                if (rowCategories.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Priority Selector Chips
                    Text(text = "Priority Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TicketPriority.values().forEach { prio ->
                            val isSelected = selectedPriority == prio
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPriority = prio },
                                label = { Text(prio.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MentorBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subject Field
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Issue Summary *") },
                        placeholder = { Text("e.g., Double debit for EMI") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description *") },
                        placeholder = { Text("Provide complete details regarding your grievance...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Document Upload Section
                    Text(text = "Upload Supporting Documents", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Attach bank receipts, statements, screenshots, or identity proof.", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Document Picker Simulation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val sampleDocs = listOf(
                                    "Bank_Statement_July2026.pdf",
                                    "Payment_Receipt_UPI.png",
                                    "Address_Proof_Aadhaar.pdf",
                                    "Technical_Error_Screenshot.jpg"
                                )
                                val nextDoc = sampleDocs.filterNot { attachedFiles.contains(it) }.firstOrNull()
                                if (nextDoc != null) {
                                    attachedFiles = (attachedFiles + nextDoc).toMutableList()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MentorBlue)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Attach File", color = MentorBlue, fontSize = 12.sp)
                        }
                    }

                    // Attached Files List
                    if (attachedFiles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            attachedFiles.forEach { fileName ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.InsertDriveFile, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = fileName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                        IconButton(
                                            onClick = {
                                                attachedFiles = attachedFiles.filter { it != fileName }.toMutableList()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (subject.isNotBlank() && description.isNotBlank()) {
                                onSubmit(
                                    selectedCategory,
                                    subject,
                                    description,
                                    selectedPriority,
                                    attachedFiles,
                                    loanAccount
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MentorBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = subject.isNotBlank() && description.isNotBlank()
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Request")
                    }
                }
            }
        }
    }
}

/**
 * Detailed view sheet for a ticket including conversation history, attachments, and reply box.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailDialog(
    ticket: SupportTicket,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = ticket.ticketNumber, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = ticket.category.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(text = "Created: ${ticket.createdDate.take(10)}", fontSize = 11.sp, color = Color.Gray)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Ticket Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Subject & Status Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = ticket.subject, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // SLA Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "SLA Resolution Commitment", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "${ticket.slaHours} Hours Standard SLA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                            }

                            Surface(
                                color = when (ticket.slaStatus) {
                                    SLAStatus.ON_TRACK -> Color(0xFFE8F5E9)
                                    SLAStatus.BREACH_RISK -> Color(0xFFFFF3E0)
                                    SLAStatus.BREACHED -> Color(0xFFFFEBEE)
                                    SLAStatus.RESOLVED_ON_TIME -> Color(0xFFE0F7FA)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ticket.slaStatus.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (ticket.slaStatus) {
                                        SLAStatus.ON_TRACK -> Color(0xFF2E7D32)
                                        SLAStatus.BREACH_RISK -> Color(0xFFED6C02)
                                        SLAStatus.BREACHED -> Color(0xFFC62828)
                                        SLAStatus.RESOLVED_ON_TIME -> Color(0xFF00838F)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = ticket.description, fontSize = 13.sp, color = Color(0xFF334155))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Assigned Agent Info
                    ticket.assignedTo?.let { agent ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Assigned Support Officer: ", fontSize = 12.sp, color = Color.Gray)
                            Text(text = agent, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Attachments List
                    if (ticket.attachments.isNotEmpty()) {
                        Text(text = "Uploaded Supporting Documents", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ticket.attachments.forEach { att ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.InsertDriveFile, contentDescription = null, tint = MentorBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = att.fileName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = "${att.sizeKb} KB • Uploaded by ${att.uploadedBy}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = MentorBlue, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Conversation Thread
                    Text(text = "Activity & Communication History", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                    Spacer(modifier = Modifier.height(8.dp))

                    ticket.messages.forEach { msg ->
                        val isCustomer = msg.sentBy == "Customer"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isCustomer) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.85f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCustomer) Color(0xFFE0F2FE) else Color(0xFFF1F5F9)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = msg.sentBy, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MentorBlue)
                                        Text(text = msg.sentAt.take(16).replace("T", " "), fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = msg.message, fontSize = 12.sp, color = Color(0xFF1E293B))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reply Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Write a reply message...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onSendReply(replyText)
                                replyText = ""
                            }
                        },
                        modifier = Modifier
                            .background(MentorBlue, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

/**
 * Utility helper returning category icons.
 */
fun getCategoryIcon(category: TicketCategory): ImageVector {
    return when (category) {
        TicketCategory.STATEMENT_ISSUE -> Icons.Default.Description
        TicketCategory.PAYMENT_ISSUE -> Icons.Default.Payment
        TicketCategory.LOAN_QUERY -> Icons.Default.Help
        TicketCategory.TECHNICAL_ISSUE -> Icons.Default.Build
        TicketCategory.CONTACT_ADDRESS_CHANGE -> Icons.Default.ContactMail
        TicketCategory.GENERAL_INQUIRY -> Icons.Default.SupportAgent
        TicketCategory.OTHER -> Icons.Default.MoreHoriz
    }
}
