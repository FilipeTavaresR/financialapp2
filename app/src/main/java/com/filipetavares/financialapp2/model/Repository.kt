package com.filipetavares.financialapp2.model

import com.filipetavares.financialapp2.util.Constants.Collections.DATA
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_RECORDS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class Repository @Inject constructor(
) {

    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun addUserFirestore(id: String, email: String, name: String) {
        val user = User(id, email, name)
        firebaseStore
            .collection("users")
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun addFinancialRecordFirestore(
        idUser: String,
        financialRecord: FinancialRecord,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseStore
            .collection(DATA)
            .document(idUser)
            .collection(FINANCIAL_RECORDS)
            .add(financialRecord)
            .addOnSuccessListener {
                onSuccess(it.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }.await()
    }

    suspend fun editFinancialRecordFirestore(
        idUser: String,
        financialRecord: FinancialRecord,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseStore
            .collection(DATA)
            .document(idUser)
            .collection(FINANCIAL_RECORDS)
            .document(financialRecord.id)
            .set(financialRecord)
            .addOnSuccessListener {
                onSuccess("")
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }.await()
    }

    suspend fun deleteFinancialRecordFirestore(
        idUser: String,
        financialRecordID: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseStore
            .collection(DATA)
            .document(idUser)
            .collection(FINANCIAL_RECORDS)
            .document(financialRecordID)
            .delete()
            .addOnSuccessListener {
                onSuccess(it.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }.await()
    }

    suspend fun fetchFinancialRecords(userId: String): List<FinancialRecord> {
        val querySnapshot = firebaseStore
            .collection(DATA)
            .document(userId)
            .collection(FINANCIAL_RECORDS)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await()

        return querySnapshot.map { document ->
            FinancialRecord(
                id = document.id,
                description = document.getString("description") ?: "",
                value = document.getDouble("value") ?: 0.0,
                date = document.getString("date") ?: ""
            )
        }
    }


}