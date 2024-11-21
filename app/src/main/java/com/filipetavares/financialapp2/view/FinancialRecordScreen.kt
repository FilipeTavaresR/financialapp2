package com.filipetavares.financialapp2.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipetavares.financialapp2.R
import com.filipetavares.financialapp2.model.FinancialRecord
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_SCREEN
import com.filipetavares.financialapp2.util.Dates
import com.filipetavares.financialapp2.util.Dates.Dates.getCurrentDate
import com.filipetavares.financialapp2.viewmodel.AuthState
import com.filipetavares.financialapp2.viewmodel.AuthViewModel
import com.filipetavares.financialapp2.viewmodel.FinancialViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialRecordScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    financialViewModel: FinancialViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("auth")
            else -> Unit
        }
    }
    val authUiState = authViewModel.uiState.collectAsState()
    val options =
        listOf(stringResource(R.string.radio_expense), stringResource(R.string.radio_incoming))
    val financialUiState = financialViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var descriptionState by remember {
        mutableStateOf(financialUiState.value.financialRecord.description)
    }
    var valueState by remember {
        mutableStateOf(financialUiState.value.financialRecord.value.toString())
    }
    var dateState by remember {
        mutableStateOf(financialUiState.value.financialRecord.date.ifEmpty { Dates.getCurrentDate() })
    }
    var radioButtonState by remember {
        if (financialUiState.value.financialRecord.value > 0.0) {
            mutableStateOf(options[1])
        } else mutableStateOf(options[0])
    }
    val datePickerDialog = remember {
        Dates.createDatePickerDialog(context, calendar) { date ->
            dateState = date
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(FINANCIAL_SCREEN) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = dateState,
                onValueChange = {},
                label = { Text(getCurrentDate()) },
                readOnly = true,
                modifier = Modifier
                    .clickable {
                        datePickerDialog.show()
                    }
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = descriptionState,
                label = { Text(text = stringResource(R.string.hint_moviment)) },
                onValueChange = { descriptionState = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = valueState,
                onValueChange = { newValue ->
                    // Aceita apenas números
                    if (newValue.matches(Regex("^[0-9]*([.,][0-9]{0,2})?\$"))) {
                        valueState = newValue
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Exibe teclado numérico
                ),
                label = { Text("Número") }
            )
            Row(modifier = Modifier.padding(24.dp)) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = (option == radioButtonState),
                            onClick = { radioButtonState = option }
                        )
                        Text(text = option)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            val isDeleteVisible = financialUiState.value.financialRecord.id.isNotEmpty()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isDeleteVisible) Arrangement.SpaceAround else Arrangement.Center
            ) {
                if (isDeleteVisible) {
                    Button(
                        onClick = {
                            financialViewModel.deleteRecord(
                                authUiState.value.userId,
                                financialUiState.value.financialRecord.id,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Excluído com sucesso $it",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        "Erro ao excluir $it",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.alpha(1f)
                    ) {
                        Text(text = stringResource(R.string.delete))
                    }
                }
                Button(
                    onClick = {
                        if (financialUiState.value.financialRecord.id.isEmpty()) {
                            financialViewModel.addRecord(
                                authUiState.value.userId,
                                FinancialRecord(
                                    description = descriptionState,
                                    value = if (radioButtonState == options[0] && valueState.toDouble() > 0.0) valueState.toDouble()
                                            * -1.0 else valueState.toDouble(),
                                    date = dateState
                                ),
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Registro adicionado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(FINANCIAL_SCREEN)
                                },
                                onFailure = {
                                    Toast.makeText(context, "Erro ao salvar $it", Toast.LENGTH_LONG)
                                        .show()
                                }
                            )
                        } else {
                            financialViewModel.editRecord(
                                authUiState.value.userId,
                                FinancialRecord(
                                    id = financialUiState.value.financialRecord.id,
                                    description = descriptionState,
                                    value = if (radioButtonState == options[0] && valueState.toDouble() > 0.0) valueState.toDouble()
                                            * -1.0 else valueState.toDouble(),
                                    date = dateState
                                ),
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Registro editado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(FINANCIAL_SCREEN)
                                },
                                onFailure = {
                                    Toast.makeText(context, "Erro ao editar $it", Toast.LENGTH_LONG)
                                        .show()
                                }
                            )
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.add_moviment_button))
                }
            }
        }
    }
}