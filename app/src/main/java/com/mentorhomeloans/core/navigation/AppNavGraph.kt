package com.mentorhomeloans.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.feature.dashboard.DashboardScreen
import com.mentorhomeloans.feature.dashboard.DashboardViewModel
import com.mentorhomeloans.feature.documents.DocumentsScreen
import com.mentorhomeloans.feature.documents.DocumentsViewModel
import com.mentorhomeloans.feature.login.LoginScreen
import com.mentorhomeloans.feature.login.LoginViewModel
import com.mentorhomeloans.feature.notifications.NotificationsScreen
import com.mentorhomeloans.feature.notifications.NotificationsViewModel
import com.mentorhomeloans.feature.onboarding.OnboardingScreen
import com.mentorhomeloans.feature.onboarding.OnboardingViewModel
import com.mentorhomeloans.feature.profile.ProfileScreen
import com.mentorhomeloans.feature.profile.ProfileViewModel
import com.mentorhomeloans.feature.repayment.RepaymentScreen
import com.mentorhomeloans.feature.repayment.RepaymentViewModel
import com.mentorhomeloans.feature.settings.SettingsScreen
import com.mentorhomeloans.feature.settings.SettingsViewModel
import com.mentorhomeloans.feature.splash.SplashScreen
import com.mentorhomeloans.feature.statements.StatementsScreen
import com.mentorhomeloans.feature.statements.StatementsViewModel
import com.mentorhomeloans.feature.support.SupportScreen
import com.mentorhomeloans.feature.support.SupportViewModel
import com.mentorhomeloans.feature.transactions.TransactionsScreen
import com.mentorhomeloans.feature.transactions.TransactionsViewModel
import com.mentorhomeloans.feature.login.RegistrationViewModel
import com.mentorhomeloans.feature.cms.CmsScreen
import com.mentorhomeloans.feature.cms.CmsViewModel

/**
 * Root Navigation Graph for the MentorApp.
 *
 * Defines all composable destinations and their interconnections.
 * Uses Hilt-injected ViewModels via [hiltViewModel].
 *
 * @param navController The application-level [NavHostController].
 * @param preferencesDataStore DataStore providing theme/notification state.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesDataStore: UserPreferencesDataStore
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ─────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // ── Onboarding ─────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            val vm: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(navController = navController, viewModel = vm)
        }

        // ── Login / OTP ────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            val vm: LoginViewModel = hiltViewModel()
            LoginScreen(navController = navController, viewModel = vm)
        }

        // ── Dashboard ──────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            val vm: DashboardViewModel = hiltViewModel()
            DashboardScreen(navController = navController, viewModel = vm)
        }

        // ── Transactions ───────────────────────────────────────────────────
        composable(Screen.Transactions.route) {
            val vm: TransactionsViewModel = hiltViewModel()
            TransactionsScreen(navController = navController, viewModel = vm)
        }

        // ── Statements ─────────────────────────────────────────────────────
        composable(
            route = Screen.Statements.route,
            arguments = listOf(androidx.navigation.navArgument("loanId") { type = androidx.navigation.NavType.StringType })
        ) {
            val vm: StatementsViewModel = hiltViewModel()
            StatementsScreen(navController = navController, viewModel = vm)
        }

        // ── Documents ──────────────────────────────────────────────────────
        composable(Screen.Documents.route) {
            val vm: DocumentsViewModel = hiltViewModel()
            DocumentsScreen(navController = navController, viewModel = vm)
        }

        // ── Repayment Schedule ─────────────────────────────────────────────
        composable(
            route = Screen.Repayment.route,
            arguments = listOf(androidx.navigation.navArgument("loanId") { type = androidx.navigation.NavType.StringType })
        ) {
            val vm: RepaymentViewModel = hiltViewModel()
            RepaymentScreen(navController = navController, viewModel = vm)
        }

        // ── Profile ────────────────────────────────────────────────────────
        composable(
            route = Screen.Profile.route,
            arguments = listOf(androidx.navigation.navArgument("loanId") { type = androidx.navigation.NavType.StringType })
        ) {
            val vm: ProfileViewModel = hiltViewModel()
            val settingsVm: SettingsViewModel = hiltViewModel()
            ProfileScreen(navController = navController, viewModel = vm, settingsViewModel = settingsVm)
        }

        // ── Support Tickets ────────────────────────────────────────────────
        composable(Screen.Support.route) {
            val vm: SupportViewModel = hiltViewModel()
            SupportScreen(navController = navController, viewModel = vm)
        }

        // ── Notifications ──────────────────────────────────────────────────
        composable(Screen.Notifications.route) {
            val vm: NotificationsViewModel = hiltViewModel()
            NotificationsScreen(navController = navController, viewModel = vm)
        }

        // ── Settings ───────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = hiltViewModel()
            SettingsScreen(navController = navController, viewModel = vm)
        }

        // ── Theme Options ──────────────────────────────────────────────────
        composable(Screen.ThemeOptions.route) {
            com.mentorhomeloans.feature.settings.ThemeOptionsScreen(navController = navController)
        }

        // ── Security Settings ──────────────────────────────────────────────
        composable(Screen.SecuritySettings.route) {
            com.mentorhomeloans.feature.settings.SecuritySettingsScreen(navController = navController)
        }

        // ── About App ──────────────────────────────────────────────────────
        composable(Screen.AboutApp.route) {
            com.mentorhomeloans.feature.settings.AboutAppScreen(navController = navController)
        }

        // ── Support & Help ─────────────────────────────────────────────────
        composable(Screen.SupportHelp.route) {
            com.mentorhomeloans.feature.settings.SupportHelpScreen(navController = navController)
        }

        // ── Loan Details ───────────────────────────────────────────────────
        composable(
            route = Screen.LoanDetails.route,
            arguments = listOf(androidx.navigation.navArgument("loanId") { type = androidx.navigation.NavType.StringType })
        ) {
            val vm: com.mentorhomeloans.feature.loandetails.LoanDetailsViewModel = hiltViewModel()
            com.mentorhomeloans.feature.loandetails.LoanDetailsScreen(navController = navController, viewModel = vm)
        }

        // ── Request Registration ───────────────────────────────────────────
        composable(Screen.RequestRegistration.route) {
            val vm: RegistrationViewModel = hiltViewModel()
            com.mentorhomeloans.feature.login.RequestRegistrationScreen(
                navController = navController,
                viewModel     = vm
            )
        }

        // ── CMS Content ────────────────────────────────────────────────────
        composable(
            route = Screen.CmsContent.route,
            arguments = listOf(navArgument("pageKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val pageKey = backStackEntry.arguments?.getString("pageKey") ?: ""
            val vm: CmsViewModel = hiltViewModel()
            CmsScreen(
                pageKey = pageKey,
                viewModel = vm,
                navController = navController
            )
        }
    }
}
