package com.mentorhomeloans.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // State for login fields
    var customerId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Handle Login Success Navigation
    LaunchedEffect(uiState) {
        if (uiState is LoginUIState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // Handle reCAPTCHA Trigger (if triggered programmatically)
    LaunchedEffect(Unit) {
        viewModel.captchaTrigger.collect {
            if (customerId.isNotBlank() && password.isNotBlank()) {
                viewModel.login(customerId.trim(), password.trim())
            }
        }
    }

    // Handle Snackbar events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
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
                    .height(280.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login_welcome_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MentorBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.login_welcome_subtitle),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // Error Box
                    if (uiState is LoginUIState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = (uiState as LoginUIState.Error).message,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = customerId,
                        onValueChange = { customerId = it },
                        placeholder = { Text(stringResource(R.string.hint_customer_id)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MentorBlue) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
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
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MentorBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (customerId.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(customerId.trim(), password.trim())
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MentorBlue,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (uiState is LoginUIState.LoginLoading || uiState is LoginUIState.CaptchaLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MentorBlue, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (uiState is LoginUIState.CaptchaLoading) "Performing security check..." else "Authenticating...",
                                    fontSize = 13.sp,
                                    color = MentorBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (customerId.isNotBlank() && password.isNotBlank()) {
                                    focusManager.clearFocus()
                                    viewModel.login(customerId.trim(), password.trim())
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please enter your Customer ID and Password")
                                    }
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

                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // Visible reCAPTCHA Branding (Required for invisible reCAPTCHA)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Secured by reCAPTCHA Enterprise",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Text(
                            text = "This app follows Google Privacy Policy and Terms.",
                            fontSize = 10.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    TextButton(
                        onClick = { navController.navigate("request_registration") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(R.string.btn_request_registration), color = MentorBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}
