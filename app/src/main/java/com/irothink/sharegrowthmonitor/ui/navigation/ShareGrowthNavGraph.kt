package com.irothink.sharegrowthmonitor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                onNavigateToCompanies = { navController.navigate(Screen.CompanyList.route) }
            )
        }
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.AddTransaction.route) {
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
    }
}
