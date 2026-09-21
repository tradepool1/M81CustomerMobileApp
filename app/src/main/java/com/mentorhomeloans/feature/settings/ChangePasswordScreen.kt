package com.mentorhomeloans.feature.settings

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.mentorhomeloans.R
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.core.ui.components.AppTopBar
import com.mentorhomeloans.ui.theme.MentorBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val siteKey = stringResource(R.string.recaptcha_site_key)
    var recaptchaClient by remember { mutableStateOf<RecaptchaClient?>(null) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Initialize reCAPTCHA
    LaunchedEffect(siteKey) {
        try {
            val result = Recaptcha.getClient(context.applicationContext as Application, siteKey, 10000L)
            recaptchaClient = result.getOrNull()
        } catch (e: Exception) {
            // Silently handle
        }
    }

    // Handle Success
    LaunchedEffect(uiState) {
        if (uiState is ChangePasswordUIState.Success) {
            Toast.makeText(context, (uiState as ChangePasswordUIState.Success).message, Toast.LENGTH_LONG).show()
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = true }
            }
        }
    }

    // Handle reCAPTCHA Trigger
    LaunchedEffect(Unit) {
        viewModel.captchaTrigger.collect {
            coroutineScope.launch {
                try {
                    val client = recaptchaClient ?: Recaptcha.getClient(
                        context.applicationContext as Application,
                        siteKey,
                        10000L
                    ).getOrNull()

                    if (client == null) {
                        viewModel.onCaptchaError("Security check not ready. Please try again.")
                        return@launch
                    }

                    recaptchaClient = client
                    client.execute(RecaptchaAction.LOGIN)
                        .onSuccess { token ->
                            viewModel.onCaptchaSuccess(token, newPassword)
                        }
                        .onFailure { e ->
                            viewModel.onCaptchaError(e.message ?: "Verification failed")
                        }
                } catch (e: Exception) {
                    viewModel.onCaptchaError(e.message ?: "Security error")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Change Password",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Update Security",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MentorBlue
            )
            Text(
                "Choose a strong password to keep your account secure.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            if (uiState is ChangePasswordUIState.Error) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Text(
                        text = (uiState as ChangePasswordUIState.Error).message,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(32.dp))

            if (uiState is ChangePasswordUIState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        when {
                            currentPassword.isBlank() || newPassword.isBlank() -> {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                            newPassword != confirmPassword -> {
                                Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                            currentPassword == newPassword -> {
                                Toast.makeText(context, "New password must be different from current", Toast.LENGTH_SHORT).show()
                            }
                            else -> viewModel.startChangePasswordFlow()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MentorBlue)
                ) {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Secured by reCAPTCHA", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
