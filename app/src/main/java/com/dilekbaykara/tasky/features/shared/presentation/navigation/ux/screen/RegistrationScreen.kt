package com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dilekbaykara.tasky.features.auth.presentation.register.AuthViewModel
import com.dilekbaykara.tasky.features.auth.presentation.register.RegistrationSheet
@Composable
fun RegistrationScreen(
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    onRegistrationSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationError: () -> Unit,
    onBackPress: () -> Unit
) {
    RegistrationSheet(
        modifier = Modifier,
        viewModel = authViewModel,
        onRegistrationSuccess = onRegistrationSuccess,
        onLoginClick = onLoginClick
    )
}
