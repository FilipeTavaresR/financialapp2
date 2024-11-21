package com.filipetavares.financialapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipetavares.financialapp2.model.AuthRepository
import com.filipetavares.financialapp2.model.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    private val repository = Repository()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if (authRepository.getUserID() == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            _uiState.value.userId = authRepository.getUserID()!!
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email ou senha não podem ser vazios")
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    _uiState.value.userId = task.result.user?.uid ?: ""
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Algo deu errado")
                }

            }
    }

    fun signup(email: String, password: String, name: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            _authState.value = AuthState.Error("Email ou senha não podem ser vazios")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        task.result.user?.let { repository.addUserFirestore(it.uid, email, name) }
                        _uiState.value.userId = task.result.user?.uid ?: ""
                    }
                    _authState.postValue(AuthState.Authenticated)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Algo deu errado")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _uiState.value.userId = ""
    }

}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}