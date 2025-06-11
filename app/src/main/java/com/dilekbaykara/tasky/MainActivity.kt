package com.dilekbaykara.tasky
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dilekbaykara.tasky.features.agenda.presentation.events.EventDetailsPage
import com.dilekbaykara.tasky.features.agenda.presentation.events.EventEditDescriptionScreen
import com.dilekbaykara.tasky.features.agenda.presentation.events.EventEditTitleScreen
import com.dilekbaykara.tasky.features.agenda.presentation.events.PhotoDetailPage
import com.dilekbaykara.tasky.features.agenda.presentation.events.PhotoViewModel
import com.dilekbaykara.tasky.features.agenda.presentation.reminders.ReminderDetailPage
import com.dilekbaykara.tasky.features.agenda.presentation.tasks.TaskDetailsPage
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.auth.presentation.register.AuthViewModel
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.TaskyScreen
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen.LoginScreen
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen.MainScreen
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.screen.RegistrationScreen
import com.dilekbaykara.tasky.features.shared.presentation.notification.TaskyNotificationManager
import com.dilekbaykara.tasky.features.shared.presentation.splash.SplashViewModel
import com.dilekbaykara.tasky.features.shared.presentation.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var notificationManager: TaskyNotificationManager
    private val authViewModel: AuthViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()
    private val photoViewModel: PhotoViewModel by viewModels()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }
        authService.refreshAuthState()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            TaskyTheme {
                val navController = rememberNavController()
                val startDestination = if (authService.isAuthenticated()) {
                    TaskyScreen.Main.route
                } else {
                    TaskyScreen.Login.route
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF279F70),
                                        Color(0xFFCAEF45)
                                    )
                                )
                            )
                    ) {
                        val isLoggedIn by authService.isLoggedIn.collectAsState(initial = authService.isAuthenticated())
                        LaunchedEffect(isLoggedIn) {
                            if (isLoggedIn && navController.currentDestination?.route != TaskyScreen.Main.route) {
                                navController.navigate(TaskyScreen.Main.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            } else if (!isLoggedIn && navController.currentDestination?.route != TaskyScreen.Login.route) {
                                navController.navigate(TaskyScreen.Login.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            composable(TaskyScreen.Login.route) {
                                LoginScreen(
                                    innerPadding = innerPadding,
                                    authViewModel = authViewModel,
                                    onLoginSuccess = {
                                        navController.navigate(TaskyScreen.Main.route) {
                                            popUpTo(TaskyScreen.Login.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    onRegisterClick = { navController.navigate(TaskyScreen.Registration.route) },
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
                                        navController.navigate(TaskyScreen.Main.route) {
                                            popUpTo(TaskyScreen.Login.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    onLoginClick = { navController.navigate(TaskyScreen.Login.route) },
                                    onRegistrationError = {},
                                    onBackPress = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(TaskyScreen.Main.route) {
                                MainScreen(
                                    onBackPress = { closeAppDialog() },
                                    navController = navController,
                                    authViewModel = authViewModel,
                                    onLogout = {
                                        authViewModel.logout()
                                    }
                                )
                            }
                            composable(
                                route = "${TaskyScreen.EventDetail.route}/{eventId}",
                                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId")
                                EventDetailsPage(navController, photoViewModel, eventId = eventId)
                            }
                            composable(
                                route = "${TaskyScreen.EventDetail.route}/new/{eventId}/{selectedDate}",
                                arguments = listOf(
                                    navArgument("eventId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                EventDetailsPage(navController, photoViewModel, eventId = eventId, isNewEvent = true, selectedDate = selectedDate)
                            }
                            composable(
                                route = "${TaskyScreen.EventDetail.route}/{eventId}/edit/{selectedDate}?edit={edit}",
                                arguments = listOf(
                                    navArgument("eventId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType },
                                    navArgument("edit") { type = NavType.BoolType; defaultValue = false }
                                )
                            ) { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                val isEdit = backStackEntry.arguments?.getBoolean("edit") ?: false
                                EventDetailsPage(navController, photoViewModel, eventId = eventId, selectedDate = selectedDate, isEditMode = isEdit)
                            }
                            composable(
                                route = "${TaskyScreen.TaskDetail.route}/{taskId}",
                                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val taskId = backStackEntry.arguments?.getString("taskId")
                                TaskDetailsPage(navController, taskId = taskId)
                            }
                            composable(
                                route = "${TaskyScreen.TaskDetail.route}/new/{taskId}/{selectedDate}",
                                arguments = listOf(
                                    navArgument("taskId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val taskId = backStackEntry.arguments?.getString("taskId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                TaskDetailsPage(navController, taskId = taskId, isNewTask = true, selectedDate = selectedDate)
                            }
                            composable(
                                route = "${TaskyScreen.TaskDetail.route}/{taskId}/edit/{selectedDate}?edit={edit}",
                                arguments = listOf(
                                    navArgument("taskId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType },
                                    navArgument("edit") { type = NavType.BoolType; defaultValue = false }
                                )
                            ) { backStackEntry ->
                                val taskId = backStackEntry.arguments?.getString("taskId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                val isEdit = backStackEntry.arguments?.getBoolean("edit") ?: false
                                TaskDetailsPage(navController, taskId = taskId, selectedDate = selectedDate, isEditMode = isEdit)
                            }
                            composable(
                                route = "${TaskyScreen.ReminderDetail.route}/{reminderId}",
                                arguments = listOf(navArgument("reminderId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val reminderId = backStackEntry.arguments?.getString("reminderId")
                                ReminderDetailPage(navController, reminderId = reminderId)
                            }
                            composable(
                                route = "${TaskyScreen.ReminderDetail.route}/new/{reminderId}/{selectedDate}",
                                arguments = listOf(
                                    navArgument("reminderId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val reminderId = backStackEntry.arguments?.getString("reminderId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                ReminderDetailPage(navController, reminderId = reminderId, isNewReminder = true, selectedDate = selectedDate)
                            }
                            composable(
                                route = "${TaskyScreen.ReminderDetail.route}/{reminderId}/edit/{selectedDate}?edit={edit}",
                                arguments = listOf(
                                    navArgument("reminderId") { type = NavType.StringType },
                                    navArgument("selectedDate") { type = NavType.StringType },
                                    navArgument("edit") { type = NavType.BoolType; defaultValue = false }
                                )
                            ) { backStackEntry ->
                                val reminderId = backStackEntry.arguments?.getString("reminderId")
                                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                                val isEdit = backStackEntry.arguments?.getBoolean("edit") ?: false
                                ReminderDetailPage(navController, reminderId = reminderId, selectedDate = selectedDate, isEditMode = isEdit)
                            }
                            composable(
                                route = "${TaskyScreen.PhotoDetail.route}/{photoUrl}",
                                arguments = listOf(navArgument("photoUrl") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val photoUrl = backStackEntry.arguments?.getString("photoUrl")?.let {
                                    java.net.URLDecoder.decode(it, "UTF-8")
                                }
                                if (photoUrl != null) {
                                    PhotoDetailPage(
                                        navController = navController,
                                        photoUrl = photoUrl,
                                        onDeletePhoto = {
                                            photoViewModel.removePhoto(photoUrl)
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                            composable(
                                route = "event_edit_title/{title}",
                                arguments = listOf(
                                    navArgument("title") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    }
                                )
                            ) { backStackEntry ->
                                val title = backStackEntry.arguments?.getString("title") ?: ""
                                val decodedTitle = try {
                                    java.net.URLDecoder.decode(title, "UTF-8")
                                } catch (e: Exception) {
                                    title
                                }
                                EventEditTitleScreen(
                                    navController = navController,
                                    initialTitle = decodedTitle
                                )
                            }
                            composable(
                                route = "event_edit_description/{description}",
                                arguments = listOf(
                                    navArgument("description") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    }
                                )
                            ) { backStackEntry ->
                                val description = backStackEntry.arguments?.getString("description") ?: ""
                                val decodedDescription = try {
                                    java.net.URLDecoder.decode(description, "UTF-8")
                                } catch (e: Exception) {
                                    description
                                }
                                EventEditDescriptionScreen(
                                    navController = navController,
                                    initialDescription = decodedDescription
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }
    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "tasky") {
            val pathSegments = data.pathSegments
            if (pathSegments.size >= 2) {
                val itemType = pathSegments[0]
                val itemId = pathSegments[1]
                Log.d("MainActivity", "Deep link received: $itemType/$itemId")
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
