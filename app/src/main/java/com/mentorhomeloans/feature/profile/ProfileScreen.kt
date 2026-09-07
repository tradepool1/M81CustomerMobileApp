package com.mentorhomeloans.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mentorhomeloans.R
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.core.ui.components.ErrorState
import com.mentorhomeloans.feature.settings.SettingsUIState
import com.mentorhomeloans.feature.settings.SettingsViewModel
import com.mentorhomeloans.ui.theme.MentorBlue

/**
 * Profile details screen matching the new design specification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Handle Delete Account Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.DeleteSuccess -> {
                    // After successful deletion, log out the user locally
                    settingsViewModel.logout()
                }
                is ProfileEffect.Error -> {
                    // In a real app, show a Snackbar or Toast
                }
            }
        }
    }

    // Navigate to Login on successful logout
    LaunchedEffect(settingsUiState) {
        if (settingsUiState is SettingsUIState.LogoutSuccess) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ── Logout Confirmation Dialog ──────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(stringResource(R.string.logout_dialog_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(stringResource(R.string.logout_dialog_desc), fontSize = 14.sp, color = Color.Gray)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        settingsViewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.logout_confirm_yes), color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // ── Delete Account Confirmation Dialog ─────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(stringResource(R.string.setting_delete_account), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    stringResource(R.string.setting_delete_account_sub),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MentorBlue
                        )
                        Text(
                            text = "Manage your personal and co-applicant details",
                            fontSize = 11.sp,
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
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF4C8DFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        // Verified badge overlapping
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                                .background(Color(0xFF2E7D32), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FE))
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ProfileUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileUIState.Success -> {
                    val user = state.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // ── Personal Information Card ───────────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = MentorBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            "Personal Information",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MentorBlue
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Primary Applicant", color = MentorBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Rows
                                ProfileDetailRow(icon = Icons.Outlined.PersonOutline, label = "Full Name", value = user.fullName)
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                ProfileDetailRow(icon = Icons.Outlined.Badge, label = "Customer ID", value = user.customerId)
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                if (!user.customerType.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.WorkOutline, label = "Customer Type", value = user.customerType)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                ProfileDetailRow(icon = Icons.Outlined.Phone, label = "Mobile Number", value = user.mobileNumber)
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                ProfileDetailRow(icon = Icons.Outlined.Email, label = "Email Address", value = user.email ?: "Not Added")
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                if (!user.genderAge.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.Wc, label = "Gender & Age", value = user.genderAge)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                if (!user.kycDocName.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.Assignment, label = "KYC Document", value = user.kycDocName)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                if (!user.kycDocNumber.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.CreditCard, label = "KYC Doc Number", value = user.kycDocNumber)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                } else {
                                    ProfileDetailRow(icon = Icons.Outlined.CreditCard, label = "KYC Doc Number", value = user.panNumber)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                if (!user.relationWithHirer.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.FamilyRestroom, label = "Relation", value = user.relationWithHirer)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                if (!user.existingCustomer.isNullOrBlank()) {
                                    ProfileDetailRow(icon = Icons.Outlined.AccountBalance, label = "Existing Customer", value = user.existingCustomer)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }
                                val displayAddress = user.presentAddressText ?: user.address.line1
                                if (displayAddress.isNotBlank()) {
                                    ProfileAddressRow(icon = Icons.Outlined.Home, label = "Present Address", address = displayAddress)
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                }

                                // KYC Status Row
                                /*Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = MentorBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text("KYC Status", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                                    
                                    val isVerified = user.kycStatus.name.contains("VERIFIED", true)
                                    val badgeBg = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                    val badgeTint = if (isVerified) Color(0xFF2E7D32) else Color(0xFFE65100)
                                    
                                    Box(
                                        modifier = Modifier
                                            .background(badgeBg, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isVerified) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = badgeTint, modifier = Modifier.size(12.dp))
                                                Spacer(Modifier.width(4.dp))
                                            }
                                            Text(user.kycStatus.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeTint)
                                        }
                                    }
                                }*/
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── Co-Applicant Details Card(s) ───────────────────────────
                        val coList = if (user.coApplicantsList.isNotEmpty()) user.coApplicantsList else listOfNotNull(user.coApplicant)
                        coList.forEach { co ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Group,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Co-Applicant Details",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MentorBlue
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(co.customerType ?: "Co-Applicant", color = Color(0xFF43A047), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    // Rows
                                    ProfileDetailRow(icon = Icons.Outlined.PersonOutline, label = "Name", value = co.name, iconTint = Color(0xFF43A047))
                                    if (!co.customerId.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.Badge, label = "Customer ID", value = co.customerId, iconTint = Color(0xFF43A047))
                                    }
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                    ProfileDetailRow(icon = Icons.Outlined.FavoriteBorder, label = "Relationship", value = co.relationship, iconTint = Color(0xFF43A047))
                                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                    ProfileDetailRow(icon = Icons.Outlined.Phone, label = "Mobile Number", value = co.mobileNumber, iconTint = Color(0xFF43A047))
                                    if (!co.email.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.Email, label = "Email Address", value = co.email, iconTint = Color(0xFF43A047))
                                    }
                                    if (!co.genderAge.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.Wc, label = "Gender & Age", value = co.genderAge, iconTint = Color(0xFF43A047))
                                    }
                                    if (!co.kycDocName.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.Assignment, label = "KYC Document", value = co.kycDocName, iconTint = Color(0xFF43A047))
                                    }
                                    if (!co.kycDocNumber.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.CreditCard, label = "KYC Doc Number", value = co.kycDocNumber, iconTint = Color(0xFF43A047))
                                    } else if (co.panNumber.isNotBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileDetailRow(icon = Icons.Outlined.CreditCard, label = "KYC Doc Number", value = co.panNumber, iconTint = Color(0xFF43A047))
                                    }
                                    if (!co.presentAddressText.isNullOrBlank()) {
                                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                        ProfileAddressRow(icon = Icons.Outlined.Home, label = "Present Address", address = co.presentAddressText, iconTint = Color(0xFF43A047))
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        // ── Security Footer Banner ──────────────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF4C8DFF),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 2.dp, y = 2.dp)
                                            .size(16.dp)
                                            .background(Color.White, CircleShape)
                                            .padding(1.dp)
                                            .background(Color(0xFF2E7D32), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                    }
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Your information is secure",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MentorBlue
                                    )
                                    Text(
                                        "We use bank-level security to protect your personal information.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF5C6BC0),
                                        lineHeight = 15.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF5C6BC0)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Delete Account ──────────────────────────────────────
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeleteDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                        .size(48.dp)
                                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        stringResource(R.string.setting_delete_account),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        stringResource(R.string.setting_delete_account_sub),
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

                        Spacer(Modifier.height(12.dp))

                        // ── Logout ──────────────────────────────────────────────
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogoutDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
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
                                        .size(48.dp)
                                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        stringResource(R.string.setting_logout),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935)
                                    )
                                    Text(
                                        stringResource(R.string.setting_logout_sub),
                                        fontSize = 12.sp,
                                        color = Color(0xFFEF9A9A)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFFE53935)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
                is ProfileUIState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadProfile() })
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MentorBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

/**
 * A specialised row for address fields that wraps the address text across multiple
 * lines instead of squishing it to the right.
 */
@Composable
fun ProfileAddressRow(
    icon: ImageVector,
    label: String,
    address: String,
    iconTint: Color = MentorBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = address,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}
