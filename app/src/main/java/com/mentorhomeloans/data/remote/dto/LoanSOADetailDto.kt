package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Statement of Account (SOA) item details returned by GetCusLoanSOADetails.
 *
 * @property voucherDate Transaction voucher date (e.g. "30 Nov 2023").
 * @property perticular Description of the transaction/installment/fee/waiver. Note: key from backend API is "Perticular".
 * @property debit Amount charged / due.
 * @property credit Amount received / credited.
 * @property balance Running balance after the transaction.
 */
data class LoanSOADetailDto(
    @SerializedName("VoucherDate") val voucherDate: String? = null,
    @SerializedName("Perticular") val perticular: String? = null,
    @SerializedName("Debit") val debit: Double? = null,
    @SerializedName("Credit") val credit: Double? = null,
    @SerializedName("Balance") val balance: Double? = null
)
