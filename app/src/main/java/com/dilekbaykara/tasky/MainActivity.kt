package com.dilekbaykara.tasky


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dilekbaykara.tasky.presentation.splash.SplashViewModel
import com.dilekbaykara.tasky.presentation.auth.register.AuthViewModel
import com.dilekbaykara.tasky.presentation.navigation.ux.TaskyScreen
import com.dilekbaykara.tasky.presentation.navigation.ux.screen.LoginScreen
import com.dilekbaykara.tasky.presentation.navigation.ux.screen.RegistrationScreen
import com.dilekbaykara.tasky.presentation.common.theme.TaskyTheme
import com.dilekbaykara.tasky.presentation.navigation.ux.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }
        setContent {
            TaskyTheme {
                val navController = rememberNavController()
                Scaffold(topBar = {}, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        NavHost(navController = navController,
                            startDestination = TaskyScreen.Login.route) {
                            composable(TaskyScreen.Login.route) {
                                LoginScreen (
                                    innerPadding = innerPadding,
                                    authViewModel = authViewModel,
                                    onLoginSuccess = { navController.navigate(TaskyScreen.Main.route)},
                                    onRegisterClick = {navController.navigate(TaskyScreen.Registration.route)},
                                    onLoginError = {},
                                    onBackPress = {
                                        closeAppDialog()
                                    }
                                )
                            }
                            composable(TaskyScreen.Registration.route) {
                                RegistrationScreen(
                                    innerPadding = innerPadding,
                                    authViewModel = authViewModel,
                                    onRegistrationSuccess = {
                                        navController.navigate(TaskyScreen.Main.route)
                                    },
                                    onLoginClick = {navController.navigate(TaskyScreen.Login.route)},
                                    onRegistrationError = {},
                                    onBackPress = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(TaskyScreen.Main.route) {
                                MainScreen { closeAppDialog() }
                            }
                        }
                    }
                }
            }
        }
    }

    fun closeAppDialog() {
        android.app.AlertDialog
            .Builder(this@MainActivity)
            .setTitle("Yooo")
            .setMessage("Close the app?")
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }.setNegativeButton("Cancel", null)
            .show()
    }

}

