package com.dilekbaykara.tasky.presentation.reminders

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField

@Composable
fun ReminderDetailPage(
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    var reminderOption by remember { mutableStateOf("30 minutes before") }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTitleDialog by remember { mutableStateOf(false) }
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Project X") }
    var description by remember { mutableStateOf("Weekly plan\nRole distribution") }
    var tempTitle by remember { mutableStateOf(title) }
    var tempDescription by remember { mutableStateOf(description) }

    val timeOptions = listOf(
        "10 minutes before",
        "30 minutes before",
        "1 hour before",
        "6 hours before",
        "1 day before"
    )

    val time = remember { mutableStateOf(LocalTime.of(8, 0)) }
    val date = remember { mutableStateOf(LocalDate.of(2022, 7, 21)) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).background(Color.White)
    ) {
        Text(
            text = date.value.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFFCAEF45), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("REMINDER", style = MaterialTheme.typography.labelMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(selected = false, onClick = {})
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f).clickable {
                    tempTitle = title
                    showTitleDialog = true
                }
            )
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f).clickable {
                tempDescription = description
                showDescriptionDialog = true
            }) {
                description.split("\n").forEach {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }

        if (showTitleDialog) {
            AlertDialog(
                onDismissRequest = { showTitleDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        title = tempTitle
                        showTitleDialog = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTitleDialog = false }) { Text("Cancel") }
                },
                title = { Text("Edit Title") },
                text = {
                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        singleLine = true
                    )
                }
            )
        }
        if (showDescriptionDialog) {
            AlertDialog(
                onDismissRequest = { showDescriptionDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        description = tempDescription
                        showDescriptionDialog = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDescriptionDialog = false }) { Text("Cancel") }
                },
                title = { Text("Edit Description") },
                text = {
                    OutlinedTextField(
                        value = tempDescription,
                        onValueChange = { tempDescription = it },
                        minLines = 2
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                    .clickable { showTimePicker = true }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = time.value.format(DateTimeFormatter.ofPattern("HH:mm")))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                    .clickable { showDatePicker = true }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = date.value.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(reminderOption, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                timeOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            reminderOption = it
                            expanded = false
                        },
                        trailingIcon = {
                            if (it == reminderOption) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "DELETE REMINDER",
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onDelete() },
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> date.value = LocalDate.of(year, month + 1, dayOfMonth) },
            date.value.year,
            date.value.monthValue - 1,
            date.value.dayOfMonth
        ).apply { setOnDismissListener { showDatePicker = false } }.show()
    }
}