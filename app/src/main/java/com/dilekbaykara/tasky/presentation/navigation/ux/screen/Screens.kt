package com.dilekbaykara.tasky.presentation.navigation.ux.screen

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dilekbaykara.tasky.presentation.agenda.AgendaScreen
import com.dilekbaykara.tasky.presentation.agenda.AgendaViewModel
import com.dilekbaykara.tasky.presentation.auth.register.AuthViewModel
import com.dilekbaykara.tasky.presentation.auth.register.Header
import com.dilekbaykara.tasky.presentation.auth.register.LoginSheet
import com.dilekbaykara.tasky.presentation.auth.register.RegistrationSheet
import com.dilekbaykara.tasky.presentation.events.PhotoViewModel
import com.dilekbaykara.tasky.presentation.reminders.ReminderDetailPage
import com.dilekbaykara.tasky.presentation.tasks.TaskDetailsPage
import com.dilekbaykara.tasky.presentation.events.EventDetailsPage as RealEventDetailScreen

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
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
            .padding(top = 60.dp),
        "Login"
    )
    LoginSheet(
        modifier = Modifier.fillMaxWidth(),
        viewModel = authViewModel,
        onRegisterClick = onRegisterClick,
        onLoginSuccess = onLoginSuccess
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
            .padding(top = 40.dp),
        text = String()
    )
    RegistrationSheet(
        modifier = Modifier.fillMaxWidth(),
        viewModel = authViewModel,
        onLoginClick = onLoginClick,
        onRegistrationSuccess = onRegistrationSuccess
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(viewModel: AgendaViewModel, onBackPress: () -> Unit, navController: NavController) {
    BackHandler {
        onBackPress()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AgendaScreen(viewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailScreen(navController: NavController, photoViewModel: PhotoViewModel) {
    RealEventDetailScreen(navController = navController, photoViewModel = photoViewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetailScreen() {
    TaskDetailsPage(onClose = {}, onDelete = {})
}

@Composable
fun ReminderDetailScreen() {
    ReminderDetailPage(onDelete = {}, onClose = {})
}

@Composable
fun EditTaskTitleScreen(onSave: (String) -> Unit, onCancel: () -> Unit, initialValue: String = "") {
    var value by remember { mutableStateOf(initialValue) }
    Column(Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Text("Edit Title", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))
        Row {
            Button(onClick = { onSave(value) }) { Text("Save") }
            Spacer(Modifier.width(16.dp))
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}

@Composable
fun EditTaskDescriptionScreen(onSave: (String) -> Unit, onCancel: () -> Unit, initialValue: String = "") {
    var value by remember { mutableStateOf(initialValue) }
    Column(Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Text("Edit Description", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth().weight(1f),
            minLines = 5
        )
        Spacer(Modifier.height(24.dp))
        Row {
            Button(onClick = { onSave(value) }) { Text("Save") }
            Spacer(Modifier.width(16.dp))
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}