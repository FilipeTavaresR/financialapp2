package com.filipetavares.financialapp2.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipetavares.financialapp2.R
import com.filipetavares.financialapp2.util.Constants.Collections.AUTH_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_SCREEN
import com.filipetavares.financialapp2.util.Validators
import com.filipetavares.financialapp2.viewmodel.AuthState
import com.filipetavares.financialapp2.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate(FINANCIAL_SCREEN)
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.register_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(AUTH_SCREEN) }) {
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
            Image(painter = painterResource(R.drawable.financaslogo), contentDescription = null)
            OutlinedTextField(
                value = name,
                label = { Text(text = stringResource(R.string.name_field)) },
                onValueChange = { name = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = email,
                label = { Text(text = stringResource(R.string.email_field)) },
                onValueChange = {
                    email = it
                    isEmailValid = Validators.emailValidator(email)
                },
                modifier = Modifier.padding(top = 8.dp),
                isError = !isEmailValid
            )
            OutlinedTextField(
                value = password,
                label = { Text(text = stringResource(R.string.senha)) },
                onValueChange = { password = it },
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(
                onClick = {
                    authViewModel.signup(email, password, name)
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.register_button))
            }
        }
    }
}