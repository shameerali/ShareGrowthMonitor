package com.irothink.sharegrowthmonitor.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TransactionList : Screen("transaction_list")
    object AddTransaction : Screen("add_transaction") {
        const val routePattern = "add_transaction?transactionId={transactionId}"
        fun passId(id: String) = "add_transaction?transactionId=$id"
    }
    object Profile : Screen("profile")
    object Budget : Screen("budget")
    object CompanyList : Screen("company_list")
    object ProfitLoss : Screen("profit_loss")
    object Funds : Screen("funds")
}
