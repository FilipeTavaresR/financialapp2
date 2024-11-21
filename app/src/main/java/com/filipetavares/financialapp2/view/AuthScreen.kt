package com.filipetavares.financialapp2.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipetavares.financialapp2.R
import com.filipetavares.financialapp2.util.Constants.Collections.FINANCIAL_SCREEN
import com.filipetavares.financialapp2.util.Constants.Collections.SIGNUP_SCREEN
import com.filipetavares.financialapp2.viewmodel.AuthState
import com.filipetavares.financialapp2.viewmodel.AuthViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthScreen(
    modifier: Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
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
        modifier = modifier.padding(top = 80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(R.drawable.financaslogo), null)
            OutlinedTextField(
                value = email,
                label = { Text(text = stringResource(R.string.login)) },
                onValueChange = { email = it },
                modifier = Modifier.padding(top = 28.dp)
            )
            OutlinedTextField(
                value = password,
                label = { Text(text = stringResource(R.string.senha)) },
                onValueChange = { password = it },
                modifier = Modifier.padding(top = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Button(
                onClick = {
                    authViewModel.login(email, password)
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.login_button))
            }
            TextButton(
                onClick = {
                    navController.navigate(SIGNUP_SCREEN)
                },
            ) {
                Text(
                    text = "Ainda não tem uma conta?",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}