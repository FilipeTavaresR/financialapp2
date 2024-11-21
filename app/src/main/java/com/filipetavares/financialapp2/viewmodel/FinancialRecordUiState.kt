package com.filipetavares.financialapp2.viewmodel

import com.filipetavares.financialapp2.model.FinancialRecord


data class FinancialRecordUiState (
    var financialRecord : FinancialRecord = FinancialRecord(),
    var financialRecordsWithBalance: List<Pair<FinancialRecord, Double>> = emptyList(),
    var errorMessage: String? = ""
)


