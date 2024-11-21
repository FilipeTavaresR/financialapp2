package com.filipetavares.financialapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.filipetavares.financialapp2.ui.theme.FinancialApp2Theme
import com.filipetavares.financialapp2.viewmodel.AuthViewModel
import com.filipetavares.financialapp2.viewmodel.FinancialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val financialViewModel: FinancialViewModel by viewModels()
        setContent {
            FinancialApp2Theme {
                myAppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    authViewModel = authViewModel,
                    financialViewModel = financialViewModel
                )
                }
            }
        }
    }

