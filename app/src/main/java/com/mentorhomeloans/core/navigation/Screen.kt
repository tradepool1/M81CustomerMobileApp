package com.mentorhomeloans.core.navigation

/**
 * Sealed class representing application routes.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Statements : Screen("statements")
    object Documents : Screen("documents")
    object Repayment : Screen("repayment")
    object Profile : Screen("profile")
    object Support : Screen("support")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object ThemeOptions : Screen("theme_options")
    object SecuritySettings : Screen("security_settings")
    object AboutApp : Screen("about_app")
    object SupportHelp : Screen("support_help")
    object LoanDetails : Screen("loan_details/{loanId}") {
        fun createRoute(loanId: String) = "loan_details/$loanId"
    }
    object RequestRegistration : Screen("request_registration")
}
