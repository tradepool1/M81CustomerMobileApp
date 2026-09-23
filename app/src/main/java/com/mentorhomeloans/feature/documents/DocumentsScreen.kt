package com.mentorhomeloans.feature.documents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.R
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.ui.theme.MentorBlue

/**
 * Loan Documents screen displaying master records and allowing document requests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    navController: NavController,
    viewModel: DocumentsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val requestingDocumentId by viewModel.requestingDocumentId.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Loan Documents",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MentorBlue
                        )
                        Text(
                            text = "All your important loan documents",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MentorBlue
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = MentorBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FE))
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { paddingValues ->
        when (val state = uiState) {
            is DocumentsUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MentorBlue)
                }
            }
            is DocumentsUIState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // ── Hero Banner ────────────────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.document_banner),
                            contentDescription = "Secure & Easy Access",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.FillWidth
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    // ── Section Header ─────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Available Documents",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            if (state.loanAcNo.isNotBlank()) {
                                Text(
                                    "A/C: ${state.loanAcNo}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MentorBlue
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // ── Document Cards ─────────────────────────────────────
                    items(state.documents) { doc ->
                        DocumentItemCard(
                            document = doc,
                            isRequesting = requestingDocumentId == doc.id,
                            onRequest = { viewModel.requestDocument(doc) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    // ── Security Footer Banner ─────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GppGood,
                                        contentDescription = null,
                                        tint = MentorBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Your data is safe with us",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "All documents are encrypted and processed securely.",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is DocumentsUIState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ErrorState(message = state.message, onRetry = { viewModel.loadData() })
                }
            }
        }
    }
}

@Composable
private fun DocumentItemCard(
    document: Document,
    isRequesting: Boolean,
    onRequest: () -> Unit
) {
    val (iconBg, iconTint, iconVector) = when {
        document.documentName.contains("Interest", ignoreCase = true) -> Triple(Color(0xFFE3F2FD), MentorBlue, Icons.Default.Percent)
        document.documentName.contains("LOD", ignoreCase = true) -> Triple(Color(0xFFE8F5E9), Color(0xFF43A047), Icons.Default.FolderZip)
        document.documentName.contains("Sanction", ignoreCase = true) -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.Verified)
        document.documentName.contains("SOA", ignoreCase = true) -> Triple(Color(0xFFF3E5F5), Color(0xFF8E24AA), Icons.AutoMirrored.Filled.ReceiptLong)
        else -> Triple(Color(0xFFE8EAF6), MentorBlue, Icons.Default.Description)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(iconBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Document Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.documentName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Official Loan Document",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.width(12.dp))

            // Request Button
            Button(
                onClick = onRequest,
                enabled = !isRequesting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MentorBlue),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(38.dp)
            ) {
                if (isRequesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Request",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
