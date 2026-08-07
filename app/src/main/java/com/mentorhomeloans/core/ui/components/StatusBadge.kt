package com.mentorhomeloans.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mentorhomeloans.core.ui.theme.OverdueRed
import com.mentorhomeloans.core.ui.theme.PendingOrange
import com.mentorhomeloans.core.ui.theme.SuccessGreen

/**
 * StatusBadge to show color-coded state indicators.
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "SUCCESS", "PAID", "VERIFIED", "ACTIVE" -> Pair(SuccessGreen.copy(alpha = 0.15f), SuccessGreen)
        "PENDING", "UPCOMING", "IN_PROGRESS", "OPEN" -> Pair(PendingOrange.copy(alpha = 0.15f), PendingOrange)
        else -> Pair(OverdueRed.copy(alpha = 0.15f), OverdueRed)
    }

    Box(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
