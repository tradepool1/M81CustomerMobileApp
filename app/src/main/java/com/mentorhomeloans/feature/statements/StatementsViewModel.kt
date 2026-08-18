package com.mentorhomeloans.feature.statements

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanSOADetail
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.model.StatementType
import com.mentorhomeloans.domain.usecase.statement.DownloadStatementUseCase
import com.mentorhomeloans.domain.usecase.statement.GetCusLoanSOADetailsUseCase
import com.mentorhomeloans.domain.usecase.statement.GetStatementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel managing Statements & Statement of Account (SOA) data, filters, and user interactions.
 */
@HiltViewModel
class StatementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStatementsUseCase: GetStatementsUseCase,
    private val getCusLoanSOADetailsUseCase: GetCusLoanSOADetailsUseCase,
    private val downloadStatementUseCase: DownloadStatementUseCase
) : ViewModel() {

    /** Loan identifier extracted from navigation argument, defaulting to sample loan "23212". */
    val loanId: String = savedStateHandle.get<String>("loanId") ?: "23212"

    private val _uiState = MutableStateFlow<StatementsUIState>(StatementsUIState.Loading)
    val uiState: StateFlow<StatementsUIState> = _uiState.asStateFlow()

    private val _downloadEvent = MutableSharedFlow<String>()
    val downloadEvent: SharedFlow<String> = _downloadEvent.asSharedFlow()

    // Admin permissions
    val adminAllowsDownload: Boolean = true
    val adminAllowsAmortization: Boolean = true

    private var allStatements: List<Statement> = emptyList()
    private var allSoaDetails: List<LoanSOADetail> = emptyList()

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.ALL)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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

            // Fetch SOA Details from API endpoint POST /GetCusLoanSOADetails?loanId={loanId}
            getCusLoanSOADetailsUseCase(loanId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = StatementsUIState.Loading
                    }
                    is Result.Success -> {
                        allSoaDetails = result.data
                        // Also fetch available document statements
                        fetchDocumentStatements()
                    }
                    is Result.Error -> {
                        _uiState.value = StatementsUIState.Error(
                            result.message.ifBlank { "Failed to fetch Statement of Account details from server." }
                        )
                    }
                }
            }
        }
    }

    private fun fetchDocumentStatements() {
        viewModelScope.launch {
            getStatementsUseCase(loanId).collect { result ->
                if (result is Result.Success) {
                    allStatements = result.data
                }
                applyFilterAndSearch()
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        applyFilterAndSearch()
    }

    fun selectPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
        applyFilterAndSearch()
    }

    fun applyCustomRange(start: MonthYear, end: MonthYear) {
        _customStart.value = start
        _customEnd.value = end
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val currentQuery = _searchQuery.value.trim().lowercase(Locale.getDefault())

        // 1. Filter SOA items by period filter
        val periodFilteredSoa = filterSoaByPeriod(allSoaDetails, _selectedPeriod.value)

        // 2. Filter SOA items by search query
        val finalFilteredSoa = if (currentQuery.isEmpty()) {
            periodFilteredSoa
        } else {
            periodFilteredSoa.filter { item ->
                item.particular.lowercase(Locale.getDefault()).contains(currentQuery) ||
                item.voucherDate.lowercase(Locale.getDefault()).contains(currentQuery) ||
                item.debit.toString().contains(currentQuery) ||
                item.credit.toString().contains(currentQuery) ||
                item.balance.toString().contains(currentQuery)
            }
        }

        // 3. Filter Document Statements
        val filteredStatements = filterStatementsByPeriod(allStatements, _selectedPeriod.value)

        // 4. Calculate Aggregate Metrics for the displayed view.
        //    When the filter returns no data, metrics show 0 so the user
        //    clearly sees there are no transactions for that period.
        val totalDebit = finalFilteredSoa.sumOf { it.debit }
        val totalCredit = finalFilteredSoa.sumOf { it.credit }
        val netBalance = finalFilteredSoa.lastOrNull()?.balance ?: 0.0

        _uiState.value = StatementsUIState.Success(
            statements = filteredStatements,
            allStatements = allStatements,
            soaDetails = allSoaDetails,
            filteredSoaDetails = finalFilteredSoa,   // may be empty — UI shows empty state
            totalDebit = totalDebit,
            totalCredit = totalCredit,
            netBalance = netBalance,
            searchQuery = _searchQuery.value
        )
    }

    private fun parseVoucherDate(rawDate: String?): Date? {
        if (rawDate.isNullOrBlank()) return null
        val cleanDate = rawDate.trim()
        val patterns = arrayOf(
            "dd MMM yyyy",
            "dd-MMM-yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss",
            "MMM dd, yyyy"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                val parsed = sdf.parse(cleanDate)
                if (parsed != null) return parsed
            } catch (e: Exception) {
                // Ignore and try next pattern
            }
        }
        return null
    }

    private fun filterSoaByPeriod(items: List<LoanSOADetail>, period: PeriodFilter): List<LoanSOADetail> {
        if (period == PeriodFilter.ALL || items.isEmpty()) return items

        // Each filter uses the CURRENT SYSTEM DATE as reference.
        // If no transactions exist in that date range, we return an empty list
        // so the UI can show a "No data found" empty state.
        val currentDate = Calendar.getInstance().time
        val calItem = Calendar.getInstance()
        val calRef = Calendar.getInstance()

        return when (period) {
            PeriodFilter.ALL -> items

            PeriodFilter.CURRENT_MONTH -> {
                // Transactions in the current calendar month & year (e.g. August 2026)
                calRef.time = currentDate
                val curYr = calRef.get(Calendar.YEAR)
                val curMo = calRef.get(Calendar.MONTH)

                items.filter { item ->
                    val date = parseVoucherDate(item.voucherDate)
                    if (date != null) {
                        calItem.time = date
                        calItem.get(Calendar.YEAR) == curYr && calItem.get(Calendar.MONTH) == curMo
                    } else false
                }
                // Returns [] if no transactions in Aug 2026 → UI shows empty state
            }

            PeriodFilter.SIX_MONTHS -> {
                // Transactions from the last 6 months up to today
                calRef.time = currentDate
                calRef.add(Calendar.MONTH, -6)
                val cutoffDate = calRef.time

                items.filter { item ->
                    val date = parseVoucherDate(item.voucherDate)
                    date != null && !date.before(cutoffDate) && !date.after(currentDate)
                }
                // Returns [] if all transactions are older than 6 months → UI shows empty state
            }

            PeriodFilter.FINANCIAL_YEAR -> {
                // Indian Financial Year (Apr 1 – Mar 31) containing today's date
                calRef.time = currentDate
                val curYr = calRef.get(Calendar.YEAR)
                val curMo = calRef.get(Calendar.MONTH)
                val targetFyStartYear = if (curMo >= Calendar.APRIL) curYr else curYr - 1

                val fyStart = Calendar.getInstance().apply {
                    set(targetFyStartYear, Calendar.APRIL, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                val fyEnd = Calendar.getInstance().apply {
                    set(targetFyStartYear + 1, Calendar.MARCH, 31, 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }.time

                items.filter { item ->
                    val date = parseVoucherDate(item.voucherDate)
                    date != null && !date.before(fyStart) && !date.after(fyEnd)
                }
                // Returns [] if no transactions in current FY → UI shows empty state
            }

            PeriodFilter.CUSTOM -> {
                val start = _customStart.value
                val end = _customEnd.value
                if (start != null && end != null) {
                    items.filter { item ->
                        val date = parseVoucherDate(item.voucherDate)
                        if (date != null) {
                            calItem.time = date
                            val y = calItem.get(Calendar.YEAR)
                            val m = calItem.get(Calendar.MONTH) + 1
                            val itemVal = y * 100 + m
                            val startVal = start.year * 100 + start.month
                            val endVal = end.year * 100 + end.month
                            itemVal in startVal..endVal
                        } else false
                    }
                } else emptyList()  // No range selected yet → show empty state
            }
        }
    }

    private fun filterStatementsByPeriod(items: List<Statement>, period: PeriodFilter): List<Statement> {
        return when (period) {
            PeriodFilter.ALL -> items
            PeriodFilter.CURRENT_MONTH -> items.filter { it.type == StatementType.MONTHLY }
            PeriodFilter.SIX_MONTHS -> items.filter {
                it.type == StatementType.MONTHLY || it.type == StatementType.QUARTERLY
            }
            PeriodFilter.FINANCIAL_YEAR -> items.filter { it.type == StatementType.ANNUAL }
            PeriodFilter.CUSTOM -> items
        }
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
            _downloadEvent.emit("Downloading Interest Certificate for Loan #$loanId... (PDF)")
        }
    }

    fun downloadAmortizationSchedule() {
        viewModelScope.launch {
            _downloadEvent.emit("Downloading Repayment Schedule for Loan #$loanId... (PDF)")
        }
    }
}

enum class PeriodFilter(val label: String) {
    ALL("All"),
    CURRENT_MONTH("Current Month"),
    SIX_MONTHS("Last 6 Months"),
    FINANCIAL_YEAR("Financial Year"),
    CUSTOM("Custom Range")
}
