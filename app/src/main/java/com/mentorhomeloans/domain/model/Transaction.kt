package com.mentorhomeloans.domain.model

/**
 * Represents a single financial transaction on the loan account.
 *
 * Covers both EMI payments and other charges (penal, bounce, processing fees, etc.).
 * Used in the Transactions screen for history listing, filtering and search.
 *
 * @property id                Unique transaction identifier.
 * @property transactionDate   Date of the transaction in ISO-8601 format (yyyy-MM-dd).
 * @property valueDate         Value date (bank processing date) in ISO-8601 format.
 * @property type              Type of transaction (EMI, charge, prepayment, etc.).
 * @property description       Narration / description of the transaction.
 * @property amount            Transaction amount in INR (always positive).
 * @property principalComponent Principal portion of an EMI payment.
 * @property interestComponent Interest portion of an EMI payment.
 * @property chargesComponent  Any charges included in the transaction.
 * @property balance           Outstanding principal balance after this transaction.
 * @property referenceNumber   Bank / system reference number for the transaction.
 * @property paymentMode       Mode of payment used (NEFT, NACH, Cash, etc.).
 * @property status            Processing status of the transaction.
 * @property receiptUrl        Optional URL to download the payment receipt PDF.
 */
data class Transaction(
    val id: String,
    val transactionDate: String,
    val valueDate: String,
    val type: TransactionType,
    val description: String,
    val amount: Double,
    val principalComponent: Double,
    val interestComponent: Double,
    val chargesComponent: Double,
    val balance: Double,
    val referenceNumber: String,
    val paymentMode: PaymentMode,
    val status: TransactionStatus,
    val receiptUrl: String?
)

/**
 * Type of a financial transaction.
 *
 * @property displayName Human-readable label.
 */
enum class TransactionType(val displayName: String) {
    EMI_PAYMENT("EMI Payment"),
    PART_PAYMENT("Part Payment"),
    PREPAYMENT("Prepayment"),
    PENAL_CHARGE("Penal Charge"),
    BOUNCE_CHARGE("Bounce Charge"),
    PROCESSING_FEE("Processing Fee"),
    INSURANCE_PREMIUM("Insurance Premium"),
    FORECLOSURE_CHARGE("Foreclosure Charge"),
    OTHER_CHARGE("Other Charge"),
    DISBURSEMENT("Disbursement"),
    REFUND("Refund")
}

/**
 * Payment mode used for a transaction.
 *
 * @property displayName Human-readable label.
 */
enum class PaymentMode(val displayName: String) {
    NACH("NACH"),
    NEFT("NEFT"),
    RTGS("RTGS"),
    UPI("UPI"),
    CASH("Cash"),
    CHEQUE("Cheque"),
    ONLINE("Online"),
    SYSTEM("System Generated")
}

/**
 * Processing status of a transaction.
 *
 * @property displayName Human-readable label.
 */
enum class TransactionStatus(val displayName: String) {
    SUCCESS("Success"),
    PENDING("Pending"),
    FAILED("Failed"),
    REVERSED("Reversed"),
    BOUNCED("Bounced")
}
