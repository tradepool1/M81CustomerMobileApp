package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.SaveFcmTokenResponseDto
import com.mentorhomeloans.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SaveFcmTokenUseCaseTest {

    private val authRepository: AuthRepository = mock()
    private val useCase = SaveFcmTokenUseCase(authRepository)

    @Test
    fun `invoke with default parameter calls repository with test FCM token abcd`() = runTest {
        val expectedResponse = listOf(
            SaveFcmTokenResponseDto(responseCode = 1, responseMessage = "FCM token saved successfully.")
        )
        whenever(authRepository.saveFcmToken("abcd")).thenReturn(Result.Success(expectedResponse))

        val result = useCase()

        verify(authRepository).saveFcmToken("abcd")
        assertTrue(result is Result.Success)
        assertEquals(expectedResponse, (result as Result.Success).data)
    }

    @Test
    fun `invoke with custom token passes custom token to repository`() = runTest {
        val customToken = "custom_fcm_token_xyz_987"
        val expectedResponse = listOf(
            SaveFcmTokenResponseDto(responseCode = 1, responseMessage = "FCM token saved successfully.")
        )
        whenever(authRepository.saveFcmToken(customToken)).thenReturn(Result.Success(expectedResponse))

        val result = useCase(customToken)

        verify(authRepository).saveFcmToken(customToken)
        assertTrue(result is Result.Success)
        assertEquals(expectedResponse, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val exception = Exception("Network error")
        whenever(authRepository.saveFcmToken("abcd")).thenReturn(Result.Error(exception))

        val result = useCase("abcd")

        verify(authRepository).saveFcmToken("abcd")
        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).exception.message)
    }
}
