package com.filipetavares.financialapp2.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.filipetavares.financialapp2.R
import com.filipetavares.financialapp2.model.FinancialRecord
import com.filipetavares.financialapp2.ui.theme.DarkGreen
import com.filipetavares.financialapp2.ui.theme.FinancialApp2Theme
import com.filipetavares.financialapp2.util.Constants.Collections.AUTH_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_RECORD_SCREEN
import com.filipetavares.financialapp2.viewmodel.AuthState
import com.filipetavares.financialapp2.viewmodel.AuthViewModel
import com.filipetavares.financialapp2.viewmodel.FinancialRecordUiState
import com.filipetavares.financialapp2.viewmodel.FinancialViewModel
import javax.annotation.meta.When

@Composable
fun FinancialScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    financialViewModel: FinancialViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(AUTH_SCREEN)
            else -> Unit
        }
    }
    LaunchedEffect(Unit){
        financialViewModel.fetchRecords()
    }
    val financialUiState by financialViewModel.uiState.collectAsState()
    FinancialScreenContent(
        modifier,
        navController,
        financialUiState
    ) {
        when (it) {
            FinancialScreenEvent.SignOut -> authViewModel.signout()
            else -> financialViewModel.handleEvent(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinancialScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    financialUiState: FinancialRecordUiState,
    onEvent: (FinancialScreenEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { onEvent(FinancialScreenEvent.SignOut) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(FinancialScreenEvent.Create)
                navController.navigate(FINANCIAL_RECORD_SCREEN)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues)
        ) {
            HeaderRow()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(financialUiState.financialRecordsWithBalance) { (record, balance) ->
                    FinancialListItem(record, balance.toString()) { clickedRecord ->
                        onEvent(FinancialScreenEvent.Edit(clickedRecord))
                        navController.navigate(FINANCIAL_RECORD_SCREEN)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .border(1.dp, Color.Gray)
    ) {
        Text(
            text = "Data",
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
        Text(
            text = "Descrição",
            fontSize = 14.sp,
            modifier = Modifier.weight(2f),
            color = Color.Black
        )
        Text(
            text = "Valor",
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
        Text(
            text = "Saldo",
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
    }
}

@Composable
private fun FinancialListItem(
    record: FinancialRecord,
    balance: String,
    onItemClick: (FinancialRecord) -> Unit
) {
    val fontSize = 12.sp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .padding(top = 4.dp, bottom = 4.dp)
            .background(Color.White)
            .clickable { onItemClick(record) }
    ) {
        Text(
            text = record.date,
            fontSize = fontSize,
            modifier = Modifier
                .weight(1f)
                .padding(start = 1.dp, end = 1.dp)
        )
        Text(
            text = record.description,
            fontSize = fontSize,
            modifier = Modifier
                .weight(2f)
                .padding(start = 2.dp, end = 2.dp)
        )
        Text(
            text = record.value.toString(),
            fontSize = fontSize,
            color = if (record.value < 0.0) Color.Red else DarkGreen,
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, end = 2.dp)
        )
        Text(
            text = balance,
            fontSize = fontSize,
            modifier = Modifier.weight(1f)
                .padding(start = 2.dp, end = 2.dp)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    FinancialApp2Theme {
        FinancialScreenContent(
            modifier = Modifier,
            navController = rememberNavController(),
            financialUiState = FinancialRecordUiState()
        ) {}
    }
}

sealed class FinancialScreenEvent {
    data object SignOut : FinancialScreenEvent()
    data object Create : FinancialScreenEvent()
    data class Edit(val record: FinancialRecord) : FinancialScreenEvent()
}
