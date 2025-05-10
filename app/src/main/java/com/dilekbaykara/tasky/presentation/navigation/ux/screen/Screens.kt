package com.dilekbaykara.tasky.presentation.navigation.ux.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilekbaykara.tasky.presentation.auth.register.AuthViewModel
import com.dilekbaykara.tasky.presentation.auth.register.Header
import com.dilekbaykara.tasky.presentation.auth.register.LoginSheet
import com.dilekbaykara.tasky.presentation.auth.register.RegistrationSheet

@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackPress: () -> Unit,
    onLoginError: () -> Unit
) {
    BackHandler {
        onBackPress()
    }
    Header(
        modifier = Modifier
            .padding(innerPadding)
            .padding(top = 60.dp)
    )
    LoginSheet(
        modifier = Modifier.fillMaxWidth(),
        viewModel = authViewModel,
        onRegisterClick = onRegisterClick,
        onLoginSuccess = onLoginSuccess,
    )
}

@Composable
fun RegistrationScreen(
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    onRegistrationSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onBackPress: () -> Unit,
    onRegistrationError: () -> Unit
) {
    BackHandler {
        onBackPress()
    }
    Header(
        modifier = Modifier
            .padding(innerPadding)
            .padding(top = 40.dp)
    )
    RegistrationSheet(
        modifier = Modifier.fillMaxWidth(),
        viewModel = authViewModel,
        onLoginClick = onLoginClick,
        onRegistrationSuccess = onRegistrationSuccess
    )
}

@Composable
fun MainScreen(onBackPress: () -> Unit) {

    BackHandler {
        onBackPress()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text("Agenda", modifier = Modifier.fillMaxSize(), style =
        TextStyle(
            textAlign =  TextAlign.Center,
            color = Color.Blue,
            fontSize = 40.sp
        )
        )
    }
}