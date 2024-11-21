package com.filipetavares.financialapp2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.filipetavares.financialapp2.util.Constants.Collections.AUTH_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_RECORD_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.SIGNUP_SCREEN
import com.filipetavares.financialapp2.view.AuthScreen
import com.filipetavares.financialapp2.view.FinancialRecordScreen
import com.filipetavares.financialapp2.view.FinancialScreen
import com.filipetavares.financialapp2.view.SignUpScreen
import com.filipetavares.financialapp2.viewmodel.AuthViewModel
import com.filipetavares.financialapp2.viewmodel.FinancialViewModel

@Composable
fun myAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    financialViewModel: FinancialViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AUTH_SCREEN) {
        composable(AUTH_SCREEN) {
            AuthScreen(modifier, navController, authViewModel)
        }
        composable(SIGNUP_SCREEN) {
            SignUpScreen(modifier, navController, authViewModel)
        }
        composable(FINANCIAL_SCREEN) {
            FinancialScreen(modifier, navController, authViewModel, financialViewModel)
        }
        composable(FINANCIAL_RECORD_SCREEN) {
            FinancialRecordScreen(modifier, navController, authViewModel, financialViewModel)
        }
    }
}