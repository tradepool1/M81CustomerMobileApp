package com.mentorhomeloans.core.security.captcha

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptchaManagerTest {

    @Test
    fun `CaptchaResult Success holds token and explicit action`() {
        val token = "sample_test_token_987"
        val action = CaptchaManager.ACTION_LOGIN
        val result = CaptchaResult.Success(token = token, action = action)

        assertEquals(token, result.token)
        assertEquals("LOGIN", result.action)
    }

    @Test
    fun `CaptchaError types provide clear, descriptive messages`() {
        assertEquals("Invalid or missing CAPTCHA site key.", CaptchaError.InvalidSiteKey.message)
        assertEquals("Invalid or mismatching application package name.", CaptchaError.InvalidPackageName.message)
        assertEquals("Security check returned an empty verification token.", CaptchaError.EmptyToken.message)
        assertEquals("Security check (reCAPTCHA) initialization failed.", CaptchaError.InitializationFailed.message)
        assertTrue(CaptchaError.NetworkError.message.contains("Network error"))
        assertTrue(CaptchaError.GenerationFailed("Timeout").message.contains("Timeout"))
    }

    @Test
    fun `CaptchaResult Failure preserves cause when available`() {
        val cause = RuntimeException("Connection reset")
        val result = CaptchaResult.Failure(CaptchaError.NetworkError, cause)

        assertEquals(CaptchaError.NetworkError, result.error)
        assertEquals(cause, result.cause)
    }
}
