package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.api.RegistrationApiService
import com.mentorhomeloans.data.remote.dto.BranchDto
import com.mentorhomeloans.data.remote.dto.RegistrationRequestDto
import com.mentorhomeloans.data.remote.dto.StateDto
import com.mentorhomeloans.domain.repository.RegistrationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real network-backed implementation of [RegistrationRepository].
 */
@Singleton
class RemoteRegistrationRepository @Inject constructor(
    private val registrationApiService: RegistrationApiService
) : RegistrationRepository {

    override suspend fun getStates(): Result<List<StateDto>> {
        return try {
            val states = registrationApiService.getStates()
            Result.Success(states)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBranchesByStateId(stateId: Int): Result<List<BranchDto>> {
        return try {
            val branches = registrationApiService.getBranchesByStateId(stateId)
            Result.Success(branches)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun registerCustomer(
        name: String,
        email: String,
        mobileNo: String,
        stateId: Int,
        branchId: Int,
        loanAcNo: String,
        panNo: String
    ): Result<String> {
        return try {
            val response = registrationApiService.registerCustomer(
                RegistrationRequestDto(
                    name          = name,
                    email         = email,
                    mobileNo      = mobileNo,
                    stateId       = stateId,
                    branchId      = branchId,
                    loanAcNo      = loanAcNo,
                    panNo         = panNo,
                    allowAppLogin = true
                )
            )
            if (response.status) {
                Result.Success(response.message)
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
