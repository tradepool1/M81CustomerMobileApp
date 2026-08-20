package com.mentorhomeloans.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mentorhomeloans.R
import com.mentorhomeloans.core.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestRegistrationScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var loanNo by remember { mutableStateOf("") }
    var panNo by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf("") }

    val states = listOf(
        "Rajasthan",
        "Maharashtra",
        "Gujarat",
        "Madhya Pradesh",
        "Delhi",
        "Uttar Pradesh",
        "Haryana",
        "Punjab",
        "Karnataka",
        "Tamil Nadu"
    )

    val branches = listOf(
        "Jaipur Main Branch",
        "Mumbai Corporate Branch",
        "Delhi NCR Branch",
        "Ahmedabad Branch",
        "Pune Branch",
        "Indore Branch",
        "Jodhpur Branch",
        "Kota Branch",
        "Udaipur Branch"
    )
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val successMessage = stringResource(R.string.msg_request_submitted)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.request_reg_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Full Name text field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email text field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.label_email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mobile No text field
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text(stringResource(R.string.label_mobile_no)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Loan No text field
            OutlinedTextField(
                value = loanNo,
                onValueChange = { loanNo = it },
                label = { Text(stringResource(R.string.label_loan_no)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // PAN No text field
            OutlinedTextField(
                value = panNo,
                onValueChange = { panNo = it.uppercase() },
                label = { Text(stringResource(R.string.label_pan_no)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // State dropdown selection
            DropdownField(
                label = stringResource(R.string.label_state),
                options = states,
                selectedOption = selectedState,
                onOptionSelected = { selectedState = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Branch dropdown selection
            DropdownField(
                label = stringResource(R.string.label_branch),
                options = branches,
                selectedOption = selectedBranch,
                onOptionSelected = { selectedBranch = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Submit Button
            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = successMessage,
                            duration = SnackbarDuration.Short
                        )
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.action_submit), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
