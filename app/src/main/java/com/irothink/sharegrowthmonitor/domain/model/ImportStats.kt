package com.irothink.sharegrowthmonitor.domain.model

data class ImportStats(
    val transactionsImported: Int = 0,
    val companiesImported: Int = 0,
    val budgetsImported: Int = 0,
    val errors: List<String> = emptyList()
) {
    val totalImported: Int
        get() = transactionsImported + companiesImported + budgetsImported
    
    val hasErrors: Boolean
        get() = errors.isNotEmpty()
}
