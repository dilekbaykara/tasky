package com.dilekbaykara.tasky.presentation.auth.error

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.dilekbaykara.tasky.presentation.auth.register.Error

@Composable
fun ErrorDialog(error: Error, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = { Text("Error") },
            text = {
                Text(
                    when (error) {
                        is Error.Validation -> error.message
                        is Error.ApiError -> error.message
                        else -> ""
                    }
                )
            }
        )
    }
