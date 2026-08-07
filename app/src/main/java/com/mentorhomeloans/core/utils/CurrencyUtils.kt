package com.mentorhomeloans.core.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Currency utilities helper.
 */
object CurrencyUtils {

    /**
     * Formats Double amounts to Indian Rupee.
     */
    fun formatINR(amount: Double): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            formatter.maximumFractionDigits = 0
            formatter.format(amount)
        } catch (e: Exception) {
            "₹${amount.toLong()}"
        }
    }
}
