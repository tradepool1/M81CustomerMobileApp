package com.mentorhomeloans.feature.statements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.model.StatementType
import com.mentorhomeloans.domain.usecase.statement.DownloadStatementUseCase
import com.mentorhomeloans.domain.usecase.statement.GetStatementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StatementsViewModel managing download triggers, period filters, and doc sections.
 */
@HiltViewModel
class StatementsViewModel @Inject constructor(
    private val getStatementsUseCase: GetStatementsUseCase,
    private val downloadStatementUseCase: DownloadStatementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatementsUIState>(StatementsUIState.Loading)
    val uiState: StateFlow<StatementsUIState> = _uiState.asStateFlow()

    private val _downloadEvent = MutableSharedFlow<String>()
    val downloadEvent: SharedFlow<String> = _downloadEvent.asSharedFlow()

    // Admin flags (hardcoded; would come from a remote config in production)
    val adminAllowsDownload: Boolean = true
    val adminAllowsAmortization: Boolean = true

    private var allStatements: List<Statement> = emptyList()

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.CURRENT_MONTH)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    // Custom range state: (month 1-12, year e.g. 2024)
    data class MonthYear(val month: Int, val year: Int)

    private val _customStart = MutableStateFlow<MonthYear?>(null)
    val customStart: StateFlow<MonthYear?> = _customStart.asStateFlow()

    private val _customEnd = MutableStateFlow<MonthYear?>(null)
    val customEnd: StateFlow<MonthYear?> = _customEnd.asStateFlow()

    init {
        loadStatements()
    }

    fun loadStatements() {
        viewModelScope.launch {
            _uiState.value = StatementsUIState.Loading
            getStatementsUseCase("loan_99120").collect { result ->
                when (result) {
                    is Result.Success -> {
                        allStatements = result.data
                        applyPeriodFilter(_selectedPeriod.value)
                    }
                    is Result.Error -> _uiState.value = StatementsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = StatementsUIState.Loading
                }
            }
        }
    }

    fun selectPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
        applyPeriodFilter(period)
    }

    private fun applyPeriodFilter(period: PeriodFilter) {
        val filtered = when (period) {
            PeriodFilter.CURRENT_MONTH -> allStatements.filter { it.type == StatementType.MONTHLY }
            PeriodFilter.SIX_MONTHS -> allStatements.filter {
                it.type == StatementType.MONTHLY || it.type == StatementType.QUARTERLY
            }
            PeriodFilter.FINANCIAL_YEAR -> allStatements.filter { it.type == StatementType.ANNUAL }
            PeriodFilter.CUSTOM -> {
                val start = _customStart.value
                val end = _customEnd.value
                if (start != null && end != null) {
                    allStatements.filter { stmt ->
                        // Parse fromDate as "MMM yyyy" or fallback to show all
                        try {
                            val parts = stmt.fromDate.trim().split(" ")
                            val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun",
                                "Jul","Aug","Sep","Oct","Nov","Dec")
                            val m = monthNames.indexOf(parts[0]) + 1
                            val y = parts[1].toInt()
                            val stmtVal = y * 100 + m
                            val startVal = start.year * 100 + start.month
                            val endVal = end.year * 100 + end.month
                            stmtVal in startVal..endVal
                        } catch (e: Exception) { true }
                    }
                } else allStatements
            }
        }
        _uiState.value = StatementsUIState.Success(filtered, allStatements)
    }

    fun applyCustomRange(start: MonthYear, end: MonthYear) {
        _customStart.value = start
        _customEnd.value = end
        applyPeriodFilter(PeriodFilter.CUSTOM)
    }

    fun downloadStatement(statement: Statement, isPdf: Boolean) {
        viewModelScope.launch {
            val result = if (isPdf) {
                downloadStatementUseCase.downloadPdf(statement)
            } else {
                downloadStatementUseCase.downloadCsv(statement)
            }
            when (result) {
                is Result.Success -> _downloadEvent.emit("Downloaded to: ${result.data}")
                is Result.Error -> _downloadEvent.emit("Download failed: ${result.message}")
                else -> Unit
            }
        }
    }

    fun downloadInterestCertificate() {
        viewModelScope.launch {
            _downloadEvent.emit("Downloading Interest Certificate... (PDF)")
        }
    }

    fun downloadAmortizationSchedule() {
        viewModelScope.launch {
            _downloadEvent.emit("Downloading Repayment Schedule... (PDF)")
        }
    }
}

enum class PeriodFilter(val label: String) {
    CURRENT_MONTH("Current Month"),
    SIX_MONTHS("Last 6 Months"),
    FINANCIAL_YEAR("Financial Year"),
    CUSTOM("Custom Range")
}
