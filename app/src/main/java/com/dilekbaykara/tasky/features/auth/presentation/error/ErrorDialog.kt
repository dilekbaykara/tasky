package com.dilekbaykara.tasky.features.auth.presentation.error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dilekbaykara.tasky.features.auth.presentation.register.Error
@Composable
fun ErrorDialog(modifier: Modifier = Modifier, error: Error, onDismiss: () -> Unit) {
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
