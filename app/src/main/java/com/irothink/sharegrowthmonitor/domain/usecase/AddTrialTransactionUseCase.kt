package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.repository.TrialPortfolioRepository
import javax.inject.Inject

class AddTrialTransactionUseCase @Inject constructor(
    private val repository: TrialPortfolioRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.insertTransaction(transaction)
    }
}
