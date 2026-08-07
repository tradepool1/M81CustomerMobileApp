package com.mentorhomeloans.domain.model

/**
 * Represents a customer's loan account in the domain layer.
 *
 * Contains all financial details required to render the Dashboard, Repayment,
 * and Statements screens. This is the central domain entity of the application.
 *
 * @property id               Internal record ID.
 * @property accountNumber    Human-readable loan account number (e.g. "ML202400001").
 * @property loanType         Type of loan product.
 * @property sanctionAmount   Total amount sanctioned by the lender (in INR).
 * @property disbursedAmount  Amount actually disbursed to the borrower (in INR).
 * @property outstandingAmount Current principal outstanding (in INR).
 * @property interestRate     Annual interest rate as a percentage (e.g. 8.75 for 8.75%).
 * @property tenure           Total loan tenure in months.
 * @property remainingTenure  Remaining loan tenure in months.
 * @property emiAmount        Fixed monthly EMI amount (in INR).
 * @property nextEmiDate      Due date of the next EMI in ISO-8601 format (yyyy-MM-dd).
 * @property nextEmiAmount    Amount due for the next EMI (may differ if overdue).
 * @property isOverdue        True if any EMI is overdue.
 * @property overdueAmount    Total overdue amount (0.0 if not overdue).
 * @property overdueEmiCount  Number of overdue EMIs.
 * @property startDate        Loan disbursement / commencement date.
 * @property maturityDate     Loan maturity / closure date.
 * @property paidEmiCount     Number of EMIs paid so far.
 * @property totalEmiCount    Total number of EMIs in the loan tenure.
 * @property status           Current status of the loan account.
 * @property branchName       Originating branch name.
 * @property loanManagerName  Name of the assigned loan relationship manager.
 */
data class LoanAccount(
    val id: String,
    val accountNumber: String,
    val loanType: LoanType,
    val sanctionAmount: Double,
    val disbursedAmount: Double,
    val outstandingAmount: Double,
    val interestRate: Double,
    val tenure: Int,
    val remainingTenure: Int,
    val emiAmount: Double,
    val nextEmiDate: String,
    val nextEmiAmount: Double,
    val isOverdue: Boolean,
    val overdueAmount: Double,
    val overdueEmiCount: Int,
    val startDate: String,
    val maturityDate: String,
    val paidEmiCount: Int,
    val totalEmiCount: Int,
    val status: LoanStatus,
    val branchName: String,
    val loanManagerName: String,
    val principlReceived: Double = 0.0,
    val interestReceived: Double = 0.0,
    val netFinance: Double = 0.0,
    val receivedAmt: Double = 0.0
) {
    /**
     * Computes repayment progress as a float between 0f and 1f.
     *
     * @return Fraction of total loan amount repaid.
     */
    val repaymentProgress: Float
        get() = if (totalEmiCount == 0) 0f
                else (paidEmiCount.toFloat() / totalEmiCount.toFloat()).coerceIn(0f, 1f)
}

/**
 * Enumeration of loan product types offered by Mentor Home Loans.
 *
 * @property displayName Human-readable label shown in the UI.
 */
enum class LoanType(val displayName: String) {
    HOME_LOAN("Home Loan"),
    LAP("Loan Against Property"),
    HOME_EXTENSION("Home Extension Loan"),
    HOME_RENOVATION("Home Renovation Loan"),
    PLOT_PURCHASE("Plot Purchase Loan")
}

/**
 * Enumeration of possible loan account statuses.
 *
 * @property displayName Human-readable status label.
 */
enum class LoanStatus(val displayName: String) {
    ACTIVE("Active"),
    CLOSED("Closed"),
    NPA("NPA"),
    FORECLOSED("Foreclosed"),
    WRITTEN_OFF("Written Off"),
    UNDER_PROCESS("Under Process")
}
