package com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.dilekbaykara.tasky.features.agenda.presentation.AgendaScreen
import com.dilekbaykara.tasky.features.auth.presentation.register.AuthViewModel
@Composable
fun MainScreen(
    onBackPress: () -> Unit,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    AgendaScreen(
        navController = navController,
        authViewModel = authViewModel,
        onLogout = onLogout
    )
}
