package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Address
import com.mentorhomeloans.domain.model.CoApplicant
import com.mentorhomeloans.domain.model.KycStatus
import com.mentorhomeloans.domain.model.User
import com.mentorhomeloans.domain.repository.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of ProfileRepository.
 */
@Singleton
class MockProfileRepository @Inject constructor() : ProfileRepository {

    private var mockProfile = User(
        id = "cust_99210",
        customerId = "CUST00123",
        fullName = "Rohan Malhotra",
        mobileNumber = "9876543210",
        email = "rohan.malhotra@gmail.com",
        dateOfBirth = "1988-11-23",
        panNumber = "ABCDE1234F",
        address = Address(
            line1 = "Flat 402, Sunrise Apartments",
            line2 = "Link Road, Andheri West",
            city = "Mumbai",
            state = "Maharashtra",
            pinCode = "400053",
            country = "India"
        ),
        coApplicant = CoApplicant(
            name = "Priya Malhotra",
            relationship = "Spouse",
            mobileNumber = "9876500112",
            panNumber = "XYZWP5678A",
            kycDocName = "PAN CARD",
            kycDocNumber = "XYZWP5678A"
        ),
        kycStatus = KycStatus.VERIFIED,
        profileImageUrl = null,
        kycDocName = "AADHAAR CARD",
        kycDocNumber = "248804998570"
    )

    override fun getProfile(customerId: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        delay(700)
        emit(Result.Success(mockProfile))
    }

    override suspend fun updateEmail(customerId: String, newEmail: String): Result<Unit> {
        delay(800)
        mockProfile = mockProfile.copy(email = newEmail)
        return Result.Success(Unit)
    }
}
