package com.dilekbaykara.tasky.features.agenda.presentation.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dilekbaykara.tasky.features.shared.data.remote.AttendeeResponse
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttendeeBottomSheet(
    onDismiss: () -> Unit,
    onAddAttendee: (AttendeeManager.AttendeeData) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel(),
    existingAttendeeEmails: List<String> = emptyList()
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var attendeeResult by remember { mutableStateOf<AttendeeResponse?>(null) }
    var isCheckingAttendee by remember { mutableStateOf(false) }
    var lastCheckedEmail by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val isValidEmail = remember(email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val creatorEmail = eventViewModel.getCurrentUserId() // or use actual email if available

    LaunchedEffect(email) {
        attendeeResult = null
        errorMessage = null
        if (isValidEmail && email.equals(creatorEmail, ignoreCase = true)) {
            errorMessage = "You cannot add the creator as an attendee."
            return@LaunchedEffect
        }
        if (isValidEmail && existingAttendeeEmails.any { it.equals(email, ignoreCase = true) }) {
            errorMessage = "This attendee is already added."
            return@LaunchedEffect
        }
        if (isValidEmail && email != lastCheckedEmail) {
            isCheckingAttendee = true
            lastCheckedEmail = email
            kotlinx.coroutines.delay(400) // debounce
            try {
                val result = eventViewModel.checkAttendee(email)
                attendeeResult = result
                if (result == null) {
                    errorMessage = "User with this email does not exist"
                }
            } catch (e: Exception) {
                errorMessage = "Error checking attendee: ${e.message}"
            } finally {
                isCheckingAttendee = false
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ADD VISITOR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Clear, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = { Text("Email address", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        Icon(
                            imageVector = if (isValidEmail) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = null,
                            tint = if (isValidEmail) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    attendeeResult?.let { attendee ->
                        if (attendee.email != null && attendee.fullName != null && attendee.userId != null) {
                            if (attendee.email.equals(creatorEmail, ignoreCase = true)) {
                                errorMessage = "You cannot add the creator as an attendee."
                                return@Button
                            }
                            if (existingAttendeeEmails.any { it.equals(attendee.email, ignoreCase = true) }) {
                                errorMessage = "This attendee is already added."
                                return@Button
                            }
                            onAddAttendee(
                                AttendeeManager.AttendeeData(
                                    email = attendee.email,
                                    fullName = attendee.fullName,
                                    userId = attendee.userId,
                                    isGoing = true
                                )
                            )
                            onDismiss()
                        } else {
                            errorMessage = "Attendee data is incomplete."
                        }
                    }
                },
                enabled = isValidEmail && attendeeResult != null && !isCheckingAttendee,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValidEmail && attendeeResult != null && !isCheckingAttendee) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (isCheckingAttendee) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}