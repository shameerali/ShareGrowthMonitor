package com.irothink.sharegrowthmonitor.data.repository

import android.content.Context
import android.net.Uri
import com.irothink.sharegrowthmonitor.data.local.dao.BudgetDao
import com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao
import com.irothink.sharegrowthmonitor.data.local.dao.TransactionDao
import com.irothink.sharegrowthmonitor.data.local.entity.BudgetEntity
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.data.local.entity.TransactionEntity
import com.irothink.sharegrowthmonitor.domain.model.ImportStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ExcelExportImportRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionDao: TransactionDao,
    private val companyInfoDao: CompanyInfoDao,
    private val budgetDao: BudgetDao
) {

    suspend fun exportToExcel(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val workbook = XSSFWorkbook()
            
            // Export Transactions
            val transactionSheet = workbook.createSheet("Transactions")
            val transactions = transactionDao.getAllTransactionsSync()
            
            // Create header row for transactions
            val transactionHeader = transactionSheet.createRow(0)
            val transactionHeaders = listOf(
                "id", "type", "mainCompanyName", "subCompanyName", "symbol", 
                "quantity", "pricePerShare", "taxAmount", "grossAmount", "netAmount",
                "date", "brokerageFee", "notes", "createdAt"
            )
            transactionHeaders.forEachIndexed { index, header ->
                transactionHeader.createCell(index).setCellValue(header)
            }
            
            // Fill transaction data
            transactions.forEachIndexed { index, transaction ->
                val row = transactionSheet.createRow(index + 1)
                row.createCell(0).setCellValue(transaction.id)
                row.createCell(1).setCellValue(transaction.type)
                row.createCell(2).setCellValue(transaction.mainCompanyName)
                row.createCell(3).setCellValue(transaction.subCompanyName)
                row.createCell(4).setCellValue(transaction.symbol)
                row.createCell(5).setCellValue(transaction.quantity)
                row.createCell(6).setCellValue(transaction.pricePerShare)
                row.createCell(7).setCellValue(transaction.taxAmount)
                row.createCell(8).setCellValue(transaction.grossAmount)
                row.createCell(9).setCellValue(transaction.netAmount)
                row.createCell(10).setCellValue(transaction.date)
                row.createCell(11).setCellValue(transaction.brokerageFee)
                row.createCell(12).setCellValue(transaction.notes)
                row.createCell(13).setCellValue(transaction.createdAt)
            }
            
            // Export Companies
            val companySheet = workbook.createSheet("Companies")
            val companies = companyInfoDao.getAllCompaniesSync()
            
            // Create header row for companies
            val companyHeader = companySheet.createRow(0)
            val companyHeaders = listOf("id", "name", "industry", "symbol", "currentPrice", "createdAt")
            companyHeaders.forEachIndexed { index, header ->
                companyHeader.createCell(index).setCellValue(header)
            }
            
            // Fill company data
            companies.forEachIndexed { index, company ->
                val row = companySheet.createRow(index + 1)
                row.createCell(0).setCellValue(company.id.toDouble())
                row.createCell(1).setCellValue(company.name)
                row.createCell(2).setCellValue(company.industry)
                row.createCell(3).setCellValue(company.symbol)
                row.createCell(4).setCellValue(company.currentPrice)
                row.createCell(5).setCellValue(company.createdAt)
            }
            
            // Export Budget
            val budgetSheet = workbook.createSheet("Budget")
            val budgets = budgetDao.getAllBudgetsSync()
            
            // Create header row for budget
            val budgetHeader = budgetSheet.createRow(0)
            val budgetHeaders = listOf("id", "amount", "type", "date", "notes")
            budgetHeaders.forEachIndexed { index, header ->
                budgetHeader.createCell(index).setCellValue(header)
            }
            
            // Fill budget data
            budgets.forEachIndexed { index, budget ->
                val row = budgetSheet.createRow(index + 1)
                row.createCell(0).setCellValue(budget.id)
                row.createCell(1).setCellValue(budget.amount)
                row.createCell(2).setCellValue(budget.type)
                row.createCell(3).setCellValue(budget.date.toDouble())
                row.createCell(4).setCellValue(budget.notes)
            }
            
            // Write to file
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                workbook.write(outputStream)
            }
            
            workbook.close()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromExcel(uri: Uri): Result<ImportStats> = withContext(Dispatchers.IO) {
        try {
            val errors = mutableListOf<String>()
            var transactionsImported = 0
            var companiesImported = 0
            var budgetsImported = 0
            
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                
                // Import Transactions
                try {
                    val transactionSheet = workbook.getSheet("Transactions")
                    if (transactionSheet != null) {
                        val transactions = mutableListOf<TransactionEntity>()
                        
                        for (i in 1..transactionSheet.lastRowNum) {
                            val row = transactionSheet.getRow(i) ?: continue
                            try {
                                val transaction = TransactionEntity(
                                    id = getCellValueAsString(row, 0),
                                    type = getCellValueAsString(row, 1),
                                    mainCompanyName = getCellValueAsString(row, 2),
                                    subCompanyName = getCellValueAsString(row, 3),
                                    symbol = getCellValueAsString(row, 4),
                                    quantity = getCellValueAsDouble(row, 5),
                                    pricePerShare = getCellValueAsDouble(row, 6),
                                    taxAmount = getCellValueAsDouble(row, 7),
                                    grossAmount = getCellValueAsDouble(row, 8),
                                    netAmount = getCellValueAsDouble(row, 9),
                                    date = getCellValueAsString(row, 10),
                                    brokerageFee = getCellValueAsDouble(row, 11),
                                    notes = getCellValueAsString(row, 12),
                                    createdAt = getCellValueAsString(row, 13)
                                )
                                transactions.add(transaction)
                            } catch (e: Exception) {
                                errors.add("Transaction row ${i + 1}: ${e.message}")
                            }
                        }
                        
                        if (transactions.isNotEmpty()) {
                            transactionDao.insertAll(transactions)
                            transactionsImported = transactions.size
                        }
                    }
                } catch (e: Exception) {
                    errors.add("Transactions sheet: ${e.message}")
                }
                
                // Import Companies
                try {
                    val companySheet = workbook.getSheet("Companies")
                    if (companySheet != null) {
                        val companies = mutableListOf<CompanyInfoEntity>()
                        
                        for (i in 1..companySheet.lastRowNum) {
                            val row = companySheet.getRow(i) ?: continue
                            try {
                                val company = CompanyInfoEntity(
                                    id = getCellValueAsDouble(row, 0).toInt(),
                                    name = getCellValueAsString(row, 1),
                                    industry = getCellValueAsString(row, 2),
                                    symbol = getCellValueAsString(row, 3),
                                    currentPrice = getCellValueAsDouble(row, 4),
                                    createdAt = getCellValueAsString(row, 5)
                                )
                                companies.add(company)
                            } catch (e: Exception) {
                                errors.add("Company row ${i + 1}: ${e.message}")
                            }
                        }
                        
                        if (companies.isNotEmpty()) {
                            companyInfoDao.insertAll(companies)
                            companiesImported = companies.size
                        }
                    }
                } catch (e: Exception) {
                    errors.add("Companies sheet: ${e.message}")
                }
                
                // Import Budget
                try {
                    val budgetSheet = workbook.getSheet("Budget")
                    if (budgetSheet != null) {
                        val budgets = mutableListOf<BudgetEntity>()
                        
                        for (i in 1..budgetSheet.lastRowNum) {
                            val row = budgetSheet.getRow(i) ?: continue
                            try {
                                val budget = BudgetEntity(
                                    id = getCellValueAsString(row, 0),
                                    amount = getCellValueAsDouble(row, 1),
                                    type = getCellValueAsString(row, 2),
                                    date = getCellValueAsDouble(row, 3).toLong(),
                                    notes = getCellValueAsString(row, 4)
                                )
                                budgets.add(budget)
                            } catch (e: Exception) {
                                errors.add("Budget row ${i + 1}: ${e.message}")
                            }
                        }
                        
                        if (budgets.isNotEmpty()) {
                            budgetDao.insertAll(budgets)
                            budgetsImported = budgets.size
                        }
                    }
                } catch (e: Exception) {
                    errors.add("Budget sheet: ${e.message}")
                }
                
                workbook.close()
            }
            
            Result.success(
                ImportStats(
                    transactionsImported = transactionsImported,
                    companiesImported = companiesImported,
                    budgetsImported = budgetsImported,
                    errors = errors
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getCellValueAsString(row: Row, cellIndex: Int): String {
        val cell = row.getCell(cellIndex) ?: return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> ""
        }
    }
    
    private fun getCellValueAsDouble(row: Row, cellIndex: Int): Double {
        val cell = row.getCell(cellIndex) ?: return 0.0
        return when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue
            CellType.STRING -> cell.stringCellValue.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }
}
