package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.BranchDto
import com.mentorhomeloans.data.remote.dto.StateDto

/**
 * Repository interface for registration operations.
 */
interface RegistrationRepository {

    /** Fetch list of states from the server. */
    suspend fun getStates(): Result<List<StateDto>>

    /** Fetch branches for the given [stateId]. */
    suspend fun getBranchesByStateId(stateId: Int): Result<List<BranchDto>>

    /**
     * Submit a customer registration request.
     *
     * @return [Result.Success] with the server message on success,
     *         [Result.Error] on failure.
     */
    suspend fun registerCustomer(
        name: String,
        email: String,
        mobileNo: String,
        stateId: Int,
        branchId: Int,
        loanAcNo: String,
        panNo: String
    ): Result<String>
}
