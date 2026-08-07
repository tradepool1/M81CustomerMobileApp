package com.mentorhomeloans.core.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.NumberFormat
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// String Extensions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Returns null if the string is blank, otherwise the trimmed string.
 * Useful for form validation where empty input should be treated as null.
 */
fun String?.orNullIfBlank(): String? = if (isNullOrBlank()) null else this?.trim()

/**
 * Masks a mobile number for display (e.g. "9876543210" → "98XXXXX210").
 * Preserves the first 2 and last 3 digits for partial identification.
 *
 * @return Masked mobile number string.
 */
fun String.maskMobile(): String {
    return if (length < 5) this
    else substring(0, 2) + "X".repeat(length - 5) + substring(length - 3)
}

/**
 * Masks an account number for display (e.g. "ML202400001" → "XXXXXXX0001").
 * Shows only the last 4 characters.
 *
 * @return Masked account number.
 */
fun String.maskAccountNumber(): String {
    return if (length <= 4) this
    else "X".repeat(length - 4) + substring(length - 4)
}

/**
 * Converts a decimal string representation of a percentage to a display string.
 * e.g. "8.75" → "8.75%"
 *
 * @return Formatted percentage string.
 */
fun String.toPercentage(): String = "$this%"

/**
 * Converts a numeric string to Indian number format with the ₹ symbol.
 * e.g. "3500000" → "₹35,00,000"
 *
 * @return Formatted INR amount string, or this string unchanged on parse failure.
 */
fun String.toIndianCurrency(): String = toDoubleOrNull()?.toIndianCurrency() ?: this

// ─────────────────────────────────────────────────────────────────────────────
// Number Extensions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Formats a Double to Indian number format with ₹ prefix.
 * e.g. 3500000.0 → "₹35,00,000"
 *
 * @return Formatted INR string.
 */
fun Double.toIndianCurrency(): String {
    return try {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.maximumFractionDigits = 0
        "₹${formatter.format(this)}"
    } catch (e: Exception) {
        "₹${this.toLong()}"
    }
}

/**
 * Formats a Double to Indian number format with ₹ prefix and 2 decimal places.
 * e.g. 30850.50 → "₹30,850.50"
 *
 * @return Formatted INR string with paise.
 */
fun Double.toIndianCurrencyWithPaise(): String {
    return try {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        "₹${formatter.format(this)}"
    } catch (e: Exception) {
        "₹${"%.2f".format(this)}"
    }
}

/**
 * Converts an integer number of months to a human-readable tenure string.
 * e.g. 240 → "20 Years", 18 → "1 Year 6 Months"
 *
 * @return Human-readable tenure string.
 */
fun Int.toTenureString(): String {
    val years  = this / 12
    val months = this % 12
    return when {
        years > 0 && months > 0 -> "$years ${if (years == 1) "Year" else "Years"} $months ${if (months == 1) "Month" else "Months"}"
        years > 0               -> "$years ${if (years == 1) "Year" else "Years"}"
        else                    -> "$this ${if (this == 1) "Month" else "Months"}"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Context Extensions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shows a short Toast message.
 *
 * @param message The text to display.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Opens a URL in the device's default browser.
 *
 * @param url The URL to open.
 */
fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        showToast("Unable to open link")
    }
}

/**
 * Opens the device dialer with a pre-filled phone number.
 *
 * @param phoneNumber The phone number to dial.
 */
fun Context.dialPhone(phoneNumber: String) {
    try {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
    } catch (e: Exception) {
        showToast("Unable to open dialer")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Compose Modifier Extensions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Applies a click listener with no ripple effect (invisible interaction).
 * Useful for clicks on cards/containers where ripple is not desired.
 *
 * @param onClick The click action.
 * @return Modified [Modifier] with ripple-free click.
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication       = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick           = onClick
    )
}
