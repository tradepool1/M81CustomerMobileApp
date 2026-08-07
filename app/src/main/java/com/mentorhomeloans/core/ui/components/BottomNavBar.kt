package com.mentorhomeloans.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mentorhomeloans.core.navigation.Screen
import com.mentorhomeloans.ui.theme.MentorBlue
import com.mentorhomeloans.ui.theme.MentorBluePale

/**
 * Custom bottom navigation bar styled with the #006EB1 brand theme.
 *
 * – Container: a very light shade of the brand blue (MentorBluePale / #E3F2FD)
 * – Selected icon & label: brand blue #006EB1
 * – Unselected icon & label: muted blue-grey
 */
@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(Screen.Dashboard.route,    "Home",      Icons.Default.Home),
        Triple(Screen.Transactions.route, "History",   Icons.Default.History),
        Triple(Screen.Documents.route,    "Documents", Icons.Default.Description),
        Triple(Screen.Settings.route,     "Settings",  Icons.Default.Settings)
    )

    NavigationBar(
        modifier        = modifier,
        containerColor  = MentorBluePale,       // light #E3F2FD – brand-tinted background
        contentColor    = MentorBlue
    ) {
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(route) },
                icon     = { Icon(imageVector = icon, contentDescription = label) },
                label    = { Text(text = label) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor       = MentorBlue,
                    selectedTextColor       = MentorBlue,
                    indicatorColor          = Color(0xFFCCE5F5),   // soft brand-blue indicator
                    unselectedIconColor     = Color(0xFF7AABCC),
                    unselectedTextColor     = Color(0xFF7AABCC)
                )
            )
        }
    }
}
