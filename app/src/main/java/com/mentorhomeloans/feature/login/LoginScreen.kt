package com.mentorhomeloans.feature.login

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.mentorhomeloans.R
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.ui.theme.MentorBlue
import com.mentorhomeloans.ui.theme.MentorBlueDark
import kotlinx.coroutines.launch

/**
 * LoginScreen providing CAPTCHA-based login with Customer ID and Password.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val siteKey = stringResource(R.string.recaptcha_site_key)
    var recaptchaClient by remember { mutableStateOf<RecaptchaClient?>(null) }

    // State for login fields
    var customerId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Initialize reCAPTCHA Client
    LaunchedEffect(siteKey) {
        try {
            val result = Recaptcha.getClient(context.applicationContext as Application, siteKey, 10000L)
            recaptchaClient = result.getOrNull()
        } catch (e: Exception) {
            // Silently handle init failure
        }
    }

    // Handle Login Success Navigation
    LaunchedEffect(uiState) {
        if (uiState is LoginUIState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
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
                        viewModel.onCaptchaError("Security check initialization failed. Please retry.")
                        return@launch
                    }
                    
                    recaptchaClient = client
                    
                    // client.execute returns Result<String> in Kotlin
                    client.execute(RecaptchaAction.LOGIN)
                        .onSuccess { token ->
                            viewModel.onCaptchaSuccess(token, customerId, password)
                        }
                        .onFailure { e ->
                            viewModel.onCaptchaError(e.message ?: "Verification failed")
                        }
                } catch (e: Exception) {
                    viewModel.onCaptchaError(e.message ?: "Security check failed")
                }
            }
        }
    }

    // Handle Toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MentorBlue, MentorBlueDark)
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = R.drawable.splash,
                contentDescription = "Login Illustration",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login_welcome_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MentorBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_welcome_subtitle),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    when (val state = uiState) {
                        is LoginUIState.Initial, is LoginUIState.CaptchaLoading, is LoginUIState.LoginLoading, is LoginUIState.Error, is LoginUIState.UpdatePassword -> {
                            if (state is LoginUIState.Error) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            OutlinedTextField(
                                value = customerId,
                                onValueChange = { customerId = it },
                                placeholder = { Text(stringResource(R.string.hint_customer_id)) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text(stringResource(R.string.hint_password)) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MentorBlue,
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            if (state is LoginUIState.LoginLoading || state is LoginUIState.CaptchaLoading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = MentorBlue)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (state is LoginUIState.CaptchaLoading) "Verifying security..." else "Logging in...",
                                            fontSize = 12.sp,
                                            color = MentorBlue
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        if (customerId.isNotBlank() && password.isNotBlank()) {
                                            viewModel.startLoginFlow() 
                                        } else {
                                            Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MentorBlue)
                                ) {
                                    Text(
                                        text = stringResource(R.string.btn_login),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // reCAPTCHA Indicator (Visible Branding as required by Google)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF4B5563),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Protected by reCAPTCHA Enterprise",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        else -> Unit
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    TextButton(
                        onClick = { navController.navigate("request_registration") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(R.string.btn_request_registration), color = MentorBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
