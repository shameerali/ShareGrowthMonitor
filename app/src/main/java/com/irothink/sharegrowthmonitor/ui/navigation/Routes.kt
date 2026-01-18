package com.irothink.sharegrowthmonitor.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TransactionList : Screen("transaction_list")
    object AddTransaction : Screen("add_transaction")
    object Profile : Screen("profile")
    object Budget : Screen("budget")
    object CompanyList : Screen("company_list")
}
