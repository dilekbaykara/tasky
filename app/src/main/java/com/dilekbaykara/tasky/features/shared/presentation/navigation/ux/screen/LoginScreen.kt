package com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dilekbaykara.tasky.features.auth.presentation.register.AuthViewModel
import com.dilekbaykara.tasky.features.auth.presentation.register.LoginSheet
@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginError: () -> Unit,
    onBackPress: () -> Unit
) {
    LoginSheet(
        modifier = Modifier,
        viewModel = authViewModel,
        onLoginSuccess = onLoginSuccess,
        onRegisterClick = onRegisterClick
    )
}
