package com.irothink.sharegrowthmonitor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.irothink.sharegrowthmonitor.ui.dashboard.DashboardScreen
import com.irothink.sharegrowthmonitor.ui.transactions.add.AddTransactionScreen
import com.irothink.sharegrowthmonitor.ui.transactions.list.TransactionListScreen

@Composable
fun ShareGrowthNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToTransactionList = { navController.navigate(Screen.TransactionList.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToCompanies = { navController.navigate(Screen.CompanyList.route) },
                onNavigateToProfitLoss = { navController.navigate(Screen.ProfitLoss.route) },
                onNavigateToFunds = { navController.navigate(Screen.Funds.route) },
                onNavigateToTrial = { navController.navigate(Screen.TrialDashboard.route) }
            )
        }
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigateUp = { navController.navigateUp() },
                onEditTransaction = { id -> 
                    navController.navigate(Screen.AddTransaction.passId(id))
                }
            )
        }
        composable(
            route = Screen.AddTransaction.routePattern,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            AddTransactionScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.Profile.route) {
            com.irothink.sharegrowthmonitor.ui.profile.ProfileScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.Budget.route) {
            com.irothink.sharegrowthmonitor.ui.budget.BudgetScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.CompanyList.route) {
            com.irothink.sharegrowthmonitor.ui.company.list.CompanyListScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.ProfitLoss.route) {
            com.irothink.sharegrowthmonitor.ui.profitloss.ProfitLossScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.Funds.route) {
            com.irothink.sharegrowthmonitor.ui.funds.FundsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.TrialDashboard.route) {
            com.irothink.sharegrowthmonitor.ui.trial.TrialDashboardScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigateToAddForTrial = { navController.navigate(Screen.TrialAddTransaction.route) },
                onNavigateToHistory = { navController.navigate(Screen.TrialHistory.route) }
            )
        }
        composable(Screen.TrialAddTransaction.route) {
            com.irothink.sharegrowthmonitor.ui.trial.TrialAddTransactionScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.TrialHistory.route) {
            com.irothink.sharegrowthmonitor.ui.trial.TrialTransactionHistoryScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
