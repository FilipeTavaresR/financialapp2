package com.filipetavares.financialapp2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipetavares.financialapp2.model.AuthRepository
import com.filipetavares.financialapp2.model.FinancialRecord
import com.filipetavares.financialapp2.model.Repository
import com.filipetavares.financialapp2.view.FinancialScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class FinancialViewModel @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinancialRecordUiState())
    val uiState: StateFlow<FinancialRecordUiState> = _uiState

    private fun editRecord(financialRecord: FinancialRecord) {
        _uiState.value.financialRecord = financialRecord
    }

    private fun createNewRecord(){
        _uiState.value.financialRecord = FinancialRecord()
    }

    fun fetchRecords() {
        viewModelScope.launch {
            try {
                val list = repository.fetchFinancialRecords(authRepository.getUserID()!!)
                val recordsWithBalance = calculateBalanceForRecords(list)
                _uiState.value = _uiState.value
                    .copy(
                        financialRecordsWithBalance = recordsWithBalance,
                        )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }

    fun handleEvent(event: FinancialScreenEvent){
        when (event) {
            FinancialScreenEvent.Create -> createNewRecord()
            is FinancialScreenEvent.Edit -> editRecord(event.record)
            else -> Unit
        }
    }

    fun addRecord(
        userId: String,
        financialRecord: FinancialRecord,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            repository.addFinancialRecordFirestore(userId, financialRecord, onSuccess, onFailure)
        }
    }

    fun editRecord(
        userId: String,
        financialRecord: FinancialRecord,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            repository.editFinancialRecordFirestore(userId, financialRecord, onSuccess, onFailure)
        }
    }

    fun deleteRecord(
        userId: String,
        financialRecordID: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteFinancialRecordFirestore(
                userId,
                financialRecordID,
                onSuccess,
                onFailure
            )
        }
    }

    private fun calculateBalanceForRecords(records: List<FinancialRecord>): List<Pair<FinancialRecord, Double>> {
        var balance = BigDecimal.ZERO
        return records.map { record ->
            balance += BigDecimal.valueOf(record.value).setScale(2, RoundingMode.HALF_UP)
            record to balance.toDouble()
        }
    }
}

