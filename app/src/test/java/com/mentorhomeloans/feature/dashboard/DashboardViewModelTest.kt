package com.mentorhomeloans.feature.dashboard

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.datastore.UserPreferences
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.data.remote.dto.SaveFcmTokenResponseDto
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.model.LoanStatus
import com.mentorhomeloans.domain.model.LoanType
import com.mentorhomeloans.domain.usecase.auth.SaveFcmTokenUseCase
import com.mentorhomeloans.domain.usecase.loan.GetLoanAccountsUseCase
import com.mentorhomeloans.domain.usecase.loan.GetLoanSummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getLoanAccountsUseCase: GetLoanAccountsUseCase = mock()
    private val getLoanSummaryUseCase: GetLoanSummaryUseCase = mock()
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase = mock()
    private val sessionManager: SessionManager = mock()
    private val preferencesDataStore: UserPreferencesDataStore = mock()

    private val sampleLoan = LoanAccount(
        id = "24559",
        accountNumber = "MHL1044512",
        loanType = LoanType.HOME_LOAN,
        sanctionAmount = 5000000.0,
        disbursedAmount = 5000000.0,
        outstandingAmount = 4500000.0,
        interestRate = 8.5,
        tenure = 240,
        remainingTenure = 200,
        emiAmount = 43391.0,
        nextEmiDate = "2025-04-10",
        nextEmiAmount = 43391.0,
        isOverdue = false,
        overdueAmount = 0.0,
        overdueEmiCount = 0,
        startDate = "2020-01-01",
        maturityDate = "2040-01-01",
        paidEmiCount = 40,
        totalEmiCount = 240,
        status = LoanStatus.ACTIVE,
        branchName = "Main Branch",
        loanManagerName = "John Doe",
        receivedAmt = 500000.0,
        principlReceived = 300000.0,
        interestReceived = 200000.0
    )

    private val defaultUserPreferences = UserPreferences(
        isDarkModeEnabled = false,
        areNotificationsEnabled = true,
        isOnboardingCompleted = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(sessionManager.getCustomerId()).thenReturn("CUST101")
        whenever(preferencesDataStore.userPreferencesFlow).thenReturn(flowOf(defaultUserPreferences))
        whenever(getLoanAccountsUseCase("CUST101")).thenReturn(flowOf(Result.Success(listOf(sampleLoan))))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dashboard initialization triggers SaveFcmTokenUseCase with test token abcd`() = runTest {
        val expectedResponse = listOf(
            SaveFcmTokenResponseDto(responseCode = 1, responseMessage = "FCM token saved successfully.")
        )
        whenever(saveFcmTokenUseCase("abcd")).thenReturn(Result.Success(expectedResponse))

        val viewModel = DashboardViewModel(
            getLoanAccountsUseCase = getLoanAccountsUseCase,
            getLoanSummaryUseCase = getLoanSummaryUseCase,
            saveFcmTokenUseCase = saveFcmTokenUseCase,
            sessionManager = sessionManager,
            preferencesDataStore = preferencesDataStore
        )

        advanceUntilIdle()

        verify(saveFcmTokenUseCase).invoke("abcd")
        assertTrue(viewModel.uiState.value is DashboardUIState.Success)
        val successState = viewModel.uiState.value as DashboardUIState.Success
        assertEquals(sampleLoan, successState.selectedLoan)
    }
}
