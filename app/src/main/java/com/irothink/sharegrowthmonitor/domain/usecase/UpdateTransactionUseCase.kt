package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }
}
