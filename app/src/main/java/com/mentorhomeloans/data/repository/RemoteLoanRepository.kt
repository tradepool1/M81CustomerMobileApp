package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.data.local.dao.LoanDao
import com.mentorhomeloans.data.mapper.LoanMapper
import com.mentorhomeloans.data.remote.api.LoanApiService
import com.mentorhomeloans.data.remote.dto.GetLoanDetailsRequestDto
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote network implementation of [LoanRepository].
 */
@Singleton
class RemoteLoanRepository @Inject constructor(
    private val loanApiService: LoanApiService,
    private val loanDao: LoanDao,
    private val sessionManager: SessionManager
) : LoanRepository {

    override fun getLoanAccount(customerId: String): Flow<Result<LoanAccount>> = flow {
        emit(Result.Loading)
        val phoneNo = sessionManager.getMobileNumber() ?: customerId
        try {
            val dtoList = loanApiService.getAllLoanDetails(GetLoanDetailsRequestDto(phoneNo = phoneNo))
            if (dtoList.isNotEmpty()) {
                val domainList = dtoList.map { LoanMapper.fromApiDtoToDomain(it) }
                // Save to DB
                domainList.forEach { loanDao.insertLoanAccount(LoanMapper.toEntity(it)) }
                emit(Result.Success(domainList.first()))
            } else {
                emit(Result.Error(Exception("No loan accounts found")))
            }
        } catch (e: Exception) {
            // Fallback to local cache if offline/error
            loanDao.getAllLoanAccounts().collect { entities ->
                if (entities.isNotEmpty()) {
                    emit(Result.Success(LoanMapper.toDomain(entities.first())))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }

    override fun getLoanById(loanId: String): Flow<Result<LoanAccount>> = flow {
        emit(Result.Loading)
        try {
            val dtoList = loanApiService.getLoanDetailsByLoanId(loanId)
            if (dtoList.isNotEmpty()) {
                val domainModel = LoanMapper.fromLoanDetailByIdDtoToDomain(dtoList.first())
                loanDao.insertLoanAccount(LoanMapper.toEntity(domainModel))
                emit(Result.Success(domainModel))
            } else {
                loanDao.getLoanAccount(loanId).collect { entity ->
                    if (entity != null) {
                        emit(Result.Success(LoanMapper.toDomain(entity)))
                    } else {
                        emit(Result.Error(Exception("Account not found")))
                    }
                }
            }
        } catch (e: Exception) {
            loanDao.getLoanAccount(loanId).collect { entity ->
                if (entity != null) {
                    emit(Result.Success(LoanMapper.toDomain(entity)))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }

    override fun getAllLoanAccounts(customerId: String): Flow<Result<List<LoanAccount>>> = flow {
        emit(Result.Loading)
        val phoneNo = sessionManager.getMobileNumber() ?: customerId
        try {
            val dtoList = loanApiService.getAllLoanDetails(GetLoanDetailsRequestDto(phoneNo = phoneNo))
            if (dtoList.isNotEmpty()) {
                val domainList = dtoList.map { LoanMapper.fromApiDtoToDomain(it) }
                // Save to DB cache
                domainList.forEach { loanDao.insertLoanAccount(LoanMapper.toEntity(it)) }
                emit(Result.Success(domainList))
            } else {
                emit(Result.Error(Exception("No loan accounts found")))
            }
        } catch (e: Exception) {
            // Fallback to local cache if offline/error
            loanDao.getAllLoanAccounts().collect { entities ->
                if (entities.isNotEmpty()) {
                    emit(Result.Success(entities.map { LoanMapper.toDomain(it) }))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }

    override suspend fun refreshLoanAccount(customerId: String): Result<Unit> {
        val phoneNo = sessionManager.getMobileNumber() ?: customerId
        return try {
            val dtoList = loanApiService.getAllLoanDetails(GetLoanDetailsRequestDto(phoneNo = phoneNo))
            val domainList = dtoList.map { LoanMapper.fromApiDtoToDomain(it) }
            domainList.forEach { loanDao.insertLoanAccount(LoanMapper.toEntity(it)) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
