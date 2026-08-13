package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.local.entity.LoanAccountEntity
import com.mentorhomeloans.data.remote.dto.GetLoanDetailByLoanIdResponseDto
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
            loanManagerName = entity.loanManagerName,
            principlReceived = entity.principlReceived,
            interestReceived = entity.interestReceived,
            netFinance = entity.netFinance,
            receivedAmt = entity.receivedAmt,
            productName = entity.productName,
            receivedTenure = entity.receivedTenure
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
            loanManagerName = domain.loanManagerName,
            principlReceived = domain.principlReceived,
            interestReceived = domain.interestReceived,
            netFinance = domain.netFinance,
            receivedAmt = domain.receivedAmt,
            productName = domain.productName,
            receivedTenure = domain.receivedTenure
        )
    }

    fun fromApiDtoToDomain(dto: GetLoanDetailsResponseItemDto): LoanAccount {
        val loanIdStr = dto.loanId?.toString() ?: dto.loanAcNo ?: "N/A"
        val loanAcNo = dto.loanAcNo ?: loanIdStr
        val statusEnum = when (dto.loanStatus?.lowercase()) {
            "regular", "active" -> LoanStatus.ACTIVE
            "closed" -> LoanStatus.CLOSED
            "completed" -> LoanStatus.COMPLETED
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
            id = loanIdStr,
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

    fun fromLoanDetailByIdDtoToDomain(dto: GetLoanDetailByLoanIdResponseDto): LoanAccount {
        val loanIdStr = dto.loanId?.toString() ?: "N/A"
        val loanAcNoStr = dto.loanAcNo ?: loanIdStr
        val productNameStr = dto.productName ?: "Home Loan"
        val statusEnum = when (dto.loanStatus?.lowercase()) {
            "regular", "active" -> LoanStatus.ACTIVE
            "closed" -> LoanStatus.CLOSED
            "completed" -> LoanStatus.COMPLETED
            "npa" -> LoanStatus.NPA
            else -> LoanStatus.ACTIVE
        }
        val loanTypeEnum = when {
            productNameStr.contains("LAP", ignoreCase = true) -> LoanType.LAP
            productNameStr.contains("HL", ignoreCase = true) || productNameStr.contains("HOME", ignoreCase = true) -> LoanType.HOME_LOAN
            else -> LoanType.HOME_LOAN
        }

        val loanAmount = dto.loanAmount ?: 0.0
        val pos = dto.pos ?: 0.0
        val emiAmount = dto.loanEMIAmount ?: 0.0
        val interestRate = dto.caseIRR ?: 0.0
        val disbursedAmt = dto.disbursementAmt ?: 0.0
        val tenure = dto.loanTenure ?: 0
        val receivedTenure = dto.receivedTenure ?: 0
        val remainingTenure = dto.remainingTenure ?: 0

        return LoanAccount(
            id = loanIdStr,
            accountNumber = loanAcNoStr,
            loanType = loanTypeEnum,
            sanctionAmount = loanAmount,
            disbursedAmount = disbursedAmt,
            outstandingAmount = pos,
            interestRate = interestRate,
            tenure = tenure,
            remainingTenure = remainingTenure,
            emiAmount = emiAmount,
            nextEmiDate = "",
            nextEmiAmount = emiAmount,
            isOverdue = false,
            overdueAmount = 0.0,
            overdueEmiCount = 0,
            startDate = "",
            maturityDate = "",
            paidEmiCount = receivedTenure,
            totalEmiCount = tenure,
            status = statusEnum,
            branchName = "Head Office",
            loanManagerName = "",
            principlReceived = 0.0,
            interestReceived = 0.0,
            netFinance = 0.0,
            receivedAmt = 0.0,
            productName = productNameStr,
            receivedTenure = receivedTenure
        )
    }
}
