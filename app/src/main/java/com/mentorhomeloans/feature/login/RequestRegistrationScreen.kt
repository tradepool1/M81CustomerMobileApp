package com.mentorhomeloans.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mentorhomeloans.R
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.data.remote.dto.BranchDto
import com.mentorhomeloans.data.remote.dto.StateDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestRegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    // ── Form fields ───────────────────────────────────────────────────────────
    var fullName by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var mobile   by remember { mutableStateOf("") }
    var loanNo   by remember { mutableStateOf("") }
    var panNo    by remember { mutableStateOf("") }

    // ── Selected items ────────────────────────────────────────────────────────
    var selectedState  by remember { mutableStateOf<StateDto?>(null) }
    var selectedBranch by remember { mutableStateOf<BranchDto?>(null) }

    // ── Validation errors ─────────────────────────────────────────────────────
    var fullNameError  by remember { mutableStateOf<String?>(null) }
    var emailError     by remember { mutableStateOf<String?>(null) }
    var mobileError    by remember { mutableStateOf<String?>(null) }
    var loanNoError    by remember { mutableStateOf<String?>(null) }
    var panNoError     by remember { mutableStateOf<String?>(null) }
    var stateError     by remember { mutableStateOf<String?>(null) }
    var branchError    by remember { mutableStateOf<String?>(null) }

    // ── ViewModel state ───────────────────────────────────────────────────────
    val states          by viewModel.states.collectAsState()
    val branches        by viewModel.branches.collectAsState()
    val statesLoading   by viewModel.statesLoading.collectAsState()
    val branchesLoading by viewModel.branchesLoading.collectAsState()
    val uiState         by viewModel.uiState.collectAsState()

    // When states load, auto-select first
    LaunchedEffect(states) {
        if (states.isNotEmpty() && selectedState == null) {
            selectedState = states.first()
        }
    }

    // When branches reload, auto-select first
    LaunchedEffect(branches) {
        selectedBranch = branches.firstOrNull()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── Handle uiState changes ────────────────────────────────────────────────
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegistrationUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message  = state.message,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetState()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is RegistrationUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message  = state.message,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    // ── Validation helper ─────────────────────────────────────────────────────
    fun validate(): Boolean {
        var valid = true

        fullNameError = if (fullName.isBlank()) { valid = false; "Full name is required" } else null
        emailError    = when {
            email.isBlank()             -> { valid = false; "Email is required" }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                { valid = false; "Enter a valid email address" }
            else -> null
        }
        mobileError = when {
            mobile.isBlank()     -> { valid = false; "Mobile number is required" }
            mobile.length != 10  -> { valid = false; "Mobile number must be 10 digits" }
            else -> null
        }
        loanNoError   = if (loanNo.isBlank())  { valid = false; "Loan account number is required" } else null
        panNoError    = when {
            panNo.isBlank()     -> { valid = false; "PAN number is required" }
            panNo.length != 10  -> { valid = false; "PAN must be 10 characters" }
            else -> null
        }
        stateError    = if (selectedState == null)  { valid = false; "Please select a state"  } else null
        branchError   = if (selectedBranch == null) { valid = false; "Please select a branch" } else null

        return valid
    }

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Full Name
                OutlinedTextField(
                    value          = fullName,
                    onValueChange  = { fullName = it; fullNameError = null },
                    label          = { Text(stringResource(R.string.label_full_name)) },
                    isError        = fullNameError != null,
                    supportingText = { if (fullNameError != null) Text(fullNameError!!) },
                    modifier       = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Email
                OutlinedTextField(
                    value          = email,
                    onValueChange  = { email = it; emailError = null },
                    label          = { Text(stringResource(R.string.label_email)) },
                    isError        = emailError != null,
                    supportingText = { if (emailError != null) Text(emailError!!) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier       = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mobile
                OutlinedTextField(
                    value          = mobile,
                    onValueChange  = { if (it.length <= 10) { mobile = it; mobileError = null } },
                    label          = { Text(stringResource(R.string.label_mobile_no)) },
                    isError        = mobileError != null,
                    supportingText = { if (mobileError != null) Text(mobileError!!) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier       = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Loan No
                OutlinedTextField(
                    value          = loanNo,
                    onValueChange  = { loanNo = it; loanNoError = null },
                    label          = { Text(stringResource(R.string.label_loan_no)) },
                    isError        = loanNoError != null,
                    supportingText = { if (loanNoError != null) Text(loanNoError!!) },
                    modifier       = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // PAN No
                OutlinedTextField(
                    value          = panNo,
                    onValueChange  = { if (it.length <= 10) { panNo = it.uppercase(); panNoError = null } },
                    label          = { Text(stringResource(R.string.label_pan_no)) },
                    isError        = panNoError != null,
                    supportingText = { if (panNoError != null) Text(panNoError!!) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier       = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // State Dropdown
                if (statesLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Loading states...", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    ObjectDropdownField(
                        label          = stringResource(R.string.label_state),
                        options        = states,
                        selectedOption = selectedState,
                        displayText    = { it.stateName },
                        onOptionSelected = { state ->
                            selectedState  = state
                            selectedBranch = null
                            stateError     = null
                            viewModel.loadBranches(state.stateId)
                        },
                        isError        = stateError != null,
                        supportingText = stateError,
                        modifier       = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Branch Dropdown
                if (branchesLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Loading branches...", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    ObjectDropdownField(
                        label          = stringResource(R.string.label_branch),
                        options        = branches,
                        selectedOption = selectedBranch,
                        displayText    = { it.branchName },
                        onOptionSelected = { branch ->
                            selectedBranch = branch
                            branchError    = null
                        },
                        isError        = branchError != null,
                        supportingText = branchError,
                        modifier       = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                val isSubmitting = uiState is RegistrationUiState.Loading
                Button(
                    onClick = {
                        if (validate()) {
                            viewModel.submitRegistration(
                                name     = fullName.trim(),
                                email    = email.trim(),
                                mobileNo = mobile.trim(),
                                stateId  = selectedState!!.stateId,
                                branchId = selectedBranch!!.branchId,
                                loanAcNo = loanNo.trim(),
                                panNo    = panNo.trim()
                            )
                        }
                    },
                    enabled  = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier  = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color     = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.action_submit), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Generic typed dropdown ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ObjectDropdownField(
    label: String,
    options: List<T>,
    selectedOption: T?,
    displayText: (T) -> String,
    onOptionSelected: (T) -> Unit,
    isError: Boolean = false,
    supportingText: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded      = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier      = modifier
    ) {
        OutlinedTextField(
            value          = selectedOption?.let { displayText(it) } ?: "",
            onValueChange  = {},
            readOnly       = true,
            label          = { Text(label) },
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors         = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            isError        = isError,
            supportingText = { if (supportingText != null) Text(supportingText) },
            modifier       = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(displayText(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
