package com.mentorhomeloans.domain.model

/**
 * Domain model representing a single Statement of Account (SOA) transaction record.
 *
 * @property voucherDate Voucher date string (e.g. "30 Nov 2023").
 * @property particular Description / particulars of the transaction.
 * @property debit Debit amount (charges, EMI dues).
 * @property credit Credit amount (payments received, waivers).
 * @property balance Outstanding running balance.
 */
data class LoanSOADetail(
    val voucherDate: String,
    val particular: String,
    val debit: Double,
    val credit: Double,
    val balance: Double
)
