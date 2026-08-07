package com.mentorhomeloans.core.utils

import com.mentorhomeloans.core.common.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Date utilities helper.
 */
object DateUtils {

    /**
     * Formats API date strings to display dates.
     */
    fun formatDisplayDate(apiDateStr: String?): String {
        if (apiDateStr.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
            val formatter = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
            val date = parser.parse(apiDateStr) ?: return apiDateStr
            formatter.format(date)
        } catch (e: Exception) {
            apiDateStr
        }
    }
}
