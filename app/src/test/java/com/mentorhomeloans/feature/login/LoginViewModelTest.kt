package com.mentorhomeloans.feature.login

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.captcha.CaptchaError
import com.mentorhomeloans.core.security.captcha.CaptchaManager
import com.mentorhomeloans.core.security.captcha.CaptchaResult
import com.mentorhomeloans.domain.usecase.auth.LoginUseCase
import com.mentorhomeloans.domain.usecase.auth.UpdatePasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val loginUseCase: LoginUseCase = mock()
    private val updatePasswordUseCase: UpdatePasswordUseCase = mock()
    private val preferencesDataStore: UserPreferencesDataStore = mock()
    private val captchaManager: CaptchaManager = mock()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(captchaManager.currentPackageName).thenReturn("com.mentorhomeloans")
        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
            updatePasswordUseCase = updatePasswordUseCase,
            preferencesDataStore = preferencesDataStore,
            captchaManager = captchaManager
        )
        viewModel.bypassApiForTesting = false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials generates token with action LOGIN and logs in successfully`() = runTest {
        val testToken = "test_captcha_token_12345"
        whenever(captchaManager.generateToken(eq(CaptchaManager.ACTION_LOGIN)))
            .thenReturn(CaptchaResult.Success(token = testToken, action = CaptchaManager.ACTION_LOGIN))
        whenever(loginUseCase.invoke(eq("CUST101"), eq("Password123"), eq(testToken)))
            .thenReturn(Result.Success(true))

        viewModel.login("CUST101", "Password123")
        advanceUntilIdle()

        verify(captchaManager).generateToken(eq(CaptchaManager.ACTION_LOGIN))
        verify(loginUseCase).invoke("CUST101", "Password123", testToken)
        verify(preferencesDataStore).setOnboardingCompleted(true)
        assertEquals(LoginUIState.Success("CUST101"), viewModel.uiState.value)
    }

    @Test
    fun `login fails when CAPTCHA token generation fails and does NOT call login API`() = runTest {
        whenever(captchaManager.generateToken(eq(CaptchaManager.ACTION_LOGIN)))
            .thenReturn(CaptchaResult.Failure(CaptchaError.InitializationFailed))

        viewModel.login("CUST101", "Password123")
        advanceUntilIdle()

        verify(captchaManager).generateToken(eq(CaptchaManager.ACTION_LOGIN))
        verify(loginUseCase, never()).invoke(any(), any(), any())
        assertTrue(viewModel.uiState.value is LoginUIState.Error)
        assertEquals(
            "CAPTCHA error: Security check (reCAPTCHA) initialization failed.",
            (viewModel.uiState.value as LoginUIState.Error).message
        )
    }

    @Test
    fun `login fails when CAPTCHA returns empty token and does NOT call login API`() = runTest {
        whenever(captchaManager.generateToken(eq(CaptchaManager.ACTION_LOGIN)))
            .thenReturn(CaptchaResult.Success(token = "", action = CaptchaManager.ACTION_LOGIN))

        viewModel.login("CUST101", "Password123")
        advanceUntilIdle()

        verify(captchaManager).generateToken(eq(CaptchaManager.ACTION_LOGIN))
        verify(loginUseCase, never()).invoke(any(), any(), any())
        assertTrue(viewModel.uiState.value is LoginUIState.Error)
    }

    @Test
    fun `blank credentials reject immediately without triggering CAPTCHA or API`() = runTest {
        viewModel.login("", "")
        advanceUntilIdle()

        verify(captchaManager, never()).generateToken(any())
        verify(loginUseCase, never()).invoke(any(), any(), any())
        assertTrue(viewModel.uiState.value is LoginUIState.Error)
    }

    @Test
    fun `multiple login attempts request fresh CAPTCHA token each time without token reuse`() = runTest {
        val token1 = "token_attempt_1"
        val token2 = "token_attempt_2"
        whenever(captchaManager.generateToken(eq(CaptchaManager.ACTION_LOGIN)))
            .thenReturn(CaptchaResult.Success(token = token1, action = CaptchaManager.ACTION_LOGIN))
            .thenReturn(CaptchaResult.Success(token = token2, action = CaptchaManager.ACTION_LOGIN))

        whenever(loginUseCase.invoke(eq("CUST101"), eq("Pass1"), eq(token1)))
            .thenReturn(Result.Error(Exception("Invalid credentials")))
        whenever(loginUseCase.invoke(eq("CUST101"), eq("Pass2"), eq(token2)))
            .thenReturn(Result.Success(true))

        // Attempt 1
        viewModel.login("CUST101", "Pass1")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is LoginUIState.Error)

        // Attempt 2
        viewModel.login("CUST101", "Pass2")
        advanceUntilIdle()
        assertEquals(LoginUIState.Success("CUST101"), viewModel.uiState.value)

        // Verify two distinct fresh token generation calls were made
        verify(captchaManager, times(2)).generateToken(eq(CaptchaManager.ACTION_LOGIN))
        verify(loginUseCase).invoke("CUST101", "Pass1", token1)
        verify(loginUseCase).invoke("CUST101", "Pass2", token2)
    }

    @Test
    fun `login with bypassApiForTesting generates token and does NOT call login API`() = runTest {
        viewModel.bypassApiForTesting = true
        val testToken = "test_captcha_token_12345"
        whenever(captchaManager.generateToken(eq(CaptchaManager.ACTION_LOGIN)))
            .thenReturn(CaptchaResult.Success(token = testToken, action = CaptchaManager.ACTION_LOGIN))

        viewModel.login("CUST101", "Password123")
        advanceUntilIdle()

        verify(captchaManager).generateToken(eq(CaptchaManager.ACTION_LOGIN))
        verify(loginUseCase, never()).invoke(any(), any(), any())
        assertEquals(LoginUIState.Initial, viewModel.uiState.value)
    }
}
