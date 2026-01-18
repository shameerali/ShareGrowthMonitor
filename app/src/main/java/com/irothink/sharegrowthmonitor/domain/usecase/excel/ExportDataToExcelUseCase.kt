package com.irothink.sharegrowthmonitor.domain.usecase.excel

import android.net.Uri
import com.irothink.sharegrowthmonitor.data.repository.ExcelExportImportRepository
import javax.inject.Inject

class ExportDataToExcelUseCase @Inject constructor(
    private val repository: ExcelExportImportRepository
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return repository.exportToExcel(uri)
    }
}
