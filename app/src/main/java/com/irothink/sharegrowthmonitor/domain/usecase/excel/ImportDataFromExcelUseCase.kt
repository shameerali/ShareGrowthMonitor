package com.irothink.sharegrowthmonitor.domain.usecase.excel

import android.net.Uri
import com.irothink.sharegrowthmonitor.data.repository.ExcelExportImportRepository
import com.irothink.sharegrowthmonitor.domain.model.ImportStats
import javax.inject.Inject

class ImportDataFromExcelUseCase @Inject constructor(
    private val repository: ExcelExportImportRepository
) {
    suspend operator fun invoke(uri: Uri): Result<ImportStats> {
        return repository.importFromExcel(uri)
    }
}
