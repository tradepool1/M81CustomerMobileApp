package com.mentorhomeloans.domain.model

/**
 * Represents a single row in the loan amortization schedule.
 *
 * An amortization schedule breaks down each EMI into its principal,
 * interest, and charge components over the life of the loan.
 * Used in the Repayment screen to show paid vs pending EMIs.
 *
 * @property installmentNumber Sequential EMI number (1, 2, 3, ...).
 * @property dueDate           Due date for this installment (yyyy-MM-dd).
 * @property emiAmount         Total EMI amount for this installment.
 * @property principalAmount   Principal component of the EMI.
 * @property interestAmount    Interest component of the EMI.
 * @property charges           Additional charges (penal, insurance, etc.).
 * @property openingBalance    Principal balance before this EMI.
 * @property closingBalance    Principal balance after this EMI.
 * @property status            Payment status of this installment.
 * @property paidDate          Actual date payment was received (null if not paid).
 * @property paidAmount        Amount actually paid (may differ from emiAmount).
 */
data class RepaymentScheduleItem(
    val installmentNumber: Int,
    val dueDate: String,
    val emiAmount: Double,
    val principalAmount: Double,
    val interestAmount: Double,
    val charges: Double,
    val openingBalance: Double,
    val closingBalance: Double,
    val status: RepaymentStatus,
    val paidDate: String?,
    val paidAmount: Double?
)

/**
 * Comprehensive repayment summary for the loan account.
 *
 * @property loanAccountNumber      Account number for reference.
 * @property totalLoanAmount        Original loan amount.
 * @property totalAmountPaid        Total amount paid so far.
 * @property totalAmountPending     Total amount yet to be paid.
 * @property totalPrincipalPaid     Total principal repaid.
 * @property totalInterestPaid      Total interest paid.
 * @property totalPrincipalPending  Principal outstanding.
 * @property totalInterestPending   Interest yet to be charged.
 * @property prepaymentAllowed      True if partial prepayment is permitted.
 * @property prepaymentPenaltyPct   Prepayment penalty percentage (0 if none).
 * @property foreClosureAmount      Amount required for full foreclosure today.
 * @property schedule               List of all installment rows.
 */
data class RepaymentSummary(
    val loanAccountNumber: String,
    val totalLoanAmount: Double,
    val totalAmountPaid: Double,
    val totalAmountPending: Double,
    val totalPrincipalPaid: Double,
    val totalInterestPaid: Double,
    val totalPrincipalPending: Double,
    val totalInterestPending: Double,
    val prepaymentAllowed: Boolean,
    val prepaymentPenaltyPct: Double,
    val foreClosureAmount: Double,
    val schedule: List<RepaymentScheduleItem>
)

/**
 * Status of an individual EMI installment.
 *
 * @property displayName Human-readable status label.
 */
enum class RepaymentStatus(val displayName: String) {
    PAID("Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue"),
    PARTIALLY_PAID("Partially Paid"),
    UPCOMING("Upcoming")
}
