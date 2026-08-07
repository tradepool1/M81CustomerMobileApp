package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.local.entity.LoanAccountEntity
import com.mentorhomeloans.data.remote.dto.GetLoanDetailsResponseItemDto
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

    fun fromApiDtoToDomain(dto: GetLoanDetailsResponseItemDto): LoanAccount {
        val loanAcNo = dto.loanAcNo ?: "N/A"
        val statusEnum = when (dto.loanStatus?.lowercase()) {
            "regular", "active" -> LoanStatus.ACTIVE
            "closed" -> LoanStatus.CLOSED
            "npa" -> LoanStatus.NPA
            else -> LoanStatus.ACTIVE
        }
        val loanTypeEnum = when {
            loanAcNo.contains("HL", ignoreCase = true) -> LoanType.HOME_LOAN
            loanAcNo.contains("LAP", ignoreCase = true) -> LoanType.LAP
            else -> LoanType.HOME_LOAN
        }

        val loanAmount = dto.loanAmount ?: 0.0
        val pos = dto.pos ?: 0.0
        val emiAmount = dto.loanEMIAmount ?: 0.0
        val interestRate = dto.caseIRR ?: 0.0
        val disbursedAmt = dto.disbursementAmt ?: 0.0
        val emiDueDate = dto.emiDueDate ?: ""

        val principlReceived = dto.principlReceived ?: 0.0
        val interestReceived = dto.interestReceived ?: 0.0
        val netFinance = dto.netFinance ?: 0.0
        val receivedAmt = dto.receivedAmt ?: 0.0

        return LoanAccount(
            id = loanAcNo,
            accountNumber = loanAcNo,
            loanType = loanTypeEnum,
            sanctionAmount = loanAmount,
            disbursedAmount = disbursedAmt,
            outstandingAmount = pos,
            interestRate = interestRate,
            tenure = 240,
            remainingTenure = 240,
            emiAmount = emiAmount,
            nextEmiDate = emiDueDate,
            nextEmiAmount = emiAmount,
            isOverdue = false,
            overdueAmount = 0.0,
            overdueEmiCount = 0,
            startDate = "",
            maturityDate = "",
            paidEmiCount = 0,
            totalEmiCount = 240,
            status = statusEnum,
            branchName = "Head Office",
            loanManagerName = "",
            principlReceived = principlReceived,
            interestReceived = interestReceived,
            netFinance = netFinance,
            receivedAmt = receivedAmt
        )
    }
}
