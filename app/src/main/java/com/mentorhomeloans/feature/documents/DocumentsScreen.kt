package com.mentorhomeloans.feature.documents

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.model.DocumentType
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.ui.theme.MentorBlue

/**
 * Loan Documents screen matching the provided design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    navController: NavController,
    viewModel: DocumentsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.downloadEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
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
                    CircularProgressIndicator()
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
                            painter = androidx.compose.ui.res.painterResource(id = com.mentorhomeloans.R.drawable.document_banner),
                            contentDescription = "Secure & Easy Access",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
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
                                "Your Documents",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                "${state.documents.size} Documents",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MentorBlue
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // ── Document Cards ─────────────────────────────────────
                    items(state.documents) { doc ->
                        DocumentItemCard(
                            document = doc,
                            onDownload = { viewModel.downloadDocument(doc) }
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
                                        "All documents are encrypted and stored securely.",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
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
                    ErrorState(message = state.message, onRetry = { viewModel.loadDocuments() })
                }
            }
        }
    }
}

@Composable
private fun DocumentItemCard(document: Document, onDownload: () -> Unit) {
    val (iconBg, iconTint, iconVector) = when (document.type) {
        DocumentType.SANCTION_LETTER -> Triple(Color(0xFFE3F2FD), MentorBlue, Icons.Default.Description)
        DocumentType.LOAN_AGREEMENT  -> Triple(Color(0xFFE8F5E9), Color(0xFF43A047), Icons.Default.Assignment)
        DocumentType.KYC_DOCUMENT    -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.AccountBox)
        DocumentType.DISBURSEMENT_LETTER -> Triple(Color(0xFFF3E5F5), Color(0xFF8E24AA), Icons.Default.AttachMoney)
        DocumentType.INSURANCE_POLICY -> Triple(Color(0xFFE0F2F1), Color(0xFF00897B), Icons.Default.Shield)
        DocumentType.NOC             -> Triple(Color(0xFFEDE7F6), Color(0xFF5E35B1), Icons.Default.CheckCircle)
        else                         -> Triple(Color(0xFFF5F5F5), Color(0xFF757575), Icons.Default.InsertDriveFile)
    }

    val sizeDisplay = if (document.sizeKb >= 1024) {
        String.format("%.1f MB", document.sizeKb / 1024f)
    } else {
        "${document.sizeKb} KB"
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
            // Document type icon
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

            Column(modifier = Modifier.weight(1f)) {
                // Title + lock icon if protected
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = document.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (document.isPasswordProtected) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password protected",
                            modifier = Modifier.size(14.dp),
                            tint = MentorBlue
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = document.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                // PDF badge + size
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${document.fileType.extension.removePrefix(".").uppercase()} • $sizeDisplay",
                        fontSize = 11.sp,
                        color = Color(0xFF43A047),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Download button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 4.dp)
            ) {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE3F2FD), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Download ${document.title}",
                        tint = MentorBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    "Download",
                    fontSize = 10.sp,
                    color = MentorBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
