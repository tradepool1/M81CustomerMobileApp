package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.local.entity.LoanAccountEntity
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.model.LoanStatus
import com.mentorhomeloans.domain.model.LoanType

/**
 * Maps LoanAccount database entity / remote models to domain models.
 */
object LoanMapper {

    fun toDomain(entity: LoanAccountEntity): LoanAccount {
        return LoanAccount(
            id = entity.id,
            accountNumber = entity.accountNumber,
            loanType = LoanType.entries.find { it.name == entity.loanType } ?: LoanType.HOME_LOAN,
            sanctionAmount = entity.sanctionAmount,
            disbursedAmount = entity.disbursedAmount,
            outstandingAmount = entity.outstandingAmount,
            interestRate = entity.interestRate,
            tenure = entity.tenure,
            remainingTenure = entity.remainingTenure,
            emiAmount = entity.emiAmount,
            nextEmiDate = entity.nextEmiDate,
            nextEmiAmount = entity.nextEmiAmount,
            isOverdue = entity.isOverdue,
            overdueAmount = entity.overdueAmount,
            overdueEmiCount = entity.overdueEmiCount,
            startDate = entity.startDate,
            maturityDate = entity.maturityDate,
            paidEmiCount = entity.paidEmiCount,
            totalEmiCount = entity.totalEmiCount,
            status = LoanStatus.entries.find { it.name == entity.status } ?: LoanStatus.ACTIVE,
            branchName = entity.branchName,
            loanManagerName = entity.loanManagerName
        )
    }

    fun toEntity(domain: LoanAccount): LoanAccountEntity {
        return LoanAccountEntity(
            id = domain.id,
            accountNumber = domain.accountNumber,
            loanType = domain.loanType.name,
            sanctionAmount = domain.sanctionAmount,
            disbursedAmount = domain.disbursedAmount,
            outstandingAmount = domain.outstandingAmount,
            interestRate = domain.interestRate,
            tenure = domain.tenure,
            remainingTenure = domain.remainingTenure,
            emiAmount = domain.emiAmount,
            nextEmiDate = domain.nextEmiDate,
            nextEmiAmount = domain.nextEmiAmount,
            isOverdue = domain.isOverdue,
            overdueAmount = domain.overdueAmount,
            overdueEmiCount = domain.overdueEmiCount,
            startDate = domain.startDate,
            maturityDate = domain.maturityDate,
            paidEmiCount = domain.paidEmiCount,
            totalEmiCount = domain.totalEmiCount,
            status = domain.status.name,
            branchName = domain.branchName,
            loanManagerName = domain.loanManagerName
        )
    }
}
